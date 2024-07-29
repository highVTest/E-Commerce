package com.highv.ecommerce.common.aop.aspect

import com.highv.ecommerce.common.aop.annotation_class.RedissonLock
import com.highv.ecommerce.common.dto.DefaultResponse
import com.highv.ecommerce.infra.security.UserPrincipal
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.redisson.api.RScript
import org.redisson.api.RedissonClient
import org.redisson.client.codec.StringCodec
import org.slf4j.LoggerFactory
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.expression.spel.support.StandardEvaluationContext
import org.springframework.stereotype.Component
import java.lang.reflect.Method
import java.util.concurrent.TimeUnit

@Aspect
@Component
class RedissonLockAspect(
    private val redissonClient: RedissonClient,
){

    private val log = LoggerFactory.getLogger("test")
    private val parser: SpelExpressionParser = SpelExpressionParser()

    @Around("@annotation(com.highv.ecommerce.common.aop.annotation_class.RedissonLock)")
    fun redissonLock(joinPoint: ProceedingJoinPoint): Any?{

        val signature: MethodSignature = joinPoint.signature as MethodSignature
        val method: Method = signature.method
        val annotation: RedissonLock = method.getAnnotation(RedissonLock::class.java)
        val lockKey = (joinPoint.args[1] as UserPrincipal).email + parseLockKey(annotation.value, method, joinPoint.args)

        val lock = redissonClient.getLock(lockKey)

        var response = DefaultResponse("쿠폰 지급 중")

        kotlin.runCatching {

            if (!checkAndInitializeKey(lockKey)) {
                throw RuntimeException("Redis 키 유형 초기화 실패")
            }

            log.info("START : Attempting to acquire lock for key: $lockKey with waitTime: ${annotation.waitTime} and leaseTime: ${annotation.leaseTime}")

            val lockable = lock.tryLock(annotation.waitTime, annotation.leaseTime, TimeUnit.MILLISECONDS)

            // 락 키를 Redis 에다 저장 하는 로직을 생성

            log.info("END : Attempting to acquire lock for key: $lockKey with waitTime: ${annotation.waitTime} and leaseTime: ${annotation.leaseTime}")

            if(!lockable) {
                log.info("Failed to acquire lock for key: $lockKey")
                logCurrentLockState(lockKey)
                return DefaultResponse("잠금 획득 실패")
            }

            response = joinPoint.proceed() as DefaultResponse

        }.onFailure {
            throw RuntimeException("애러가 발생 하였습니다 애러 사유 : ${it.message}")
        }.also {
            lock.unlock()
        }

        return response
    }

    private fun parseLockKey(value: String, method: Method, args: Array<Any>): String {
        val context = StandardEvaluationContext()
        val parameterNames = (method.parameters.indices).map { method.parameters[it].name }

        parameterNames.forEachIndexed { index, paramName ->
            context.setVariable(paramName, args[index])
        }

        return parser.parseExpression(value).getValue(context, String::class.java) ?: ""
    }

    private fun checkAndInitializeKey(lockKey: String): Boolean {
        val redisCommands = redissonClient.getScript(StringCodec.INSTANCE)
        val script = """
            if (redis.call('type', KEYS[1]).ok ~= 'hset') then
                redis.call('del', KEYS[1]);
                redis.call('hset', KEYS[1], 'init', '1');
            end;
            return 1;
        """.trimIndent()

        val result: Long? = redisCommands.eval<Long>(RScript.Mode.READ_WRITE, script, RScript.ReturnType.VALUE, listOf(lockKey))
        return result == 1L
    }

    private fun logCurrentLockState(lockKey: String) {
        val redisCommands = redissonClient.getBucket<String>(lockKey)
        val lockInfo = redisCommands.get()
        log.info("Current lock state for key $lockKey: $lockInfo")
    }
}
package com.highv.ecommerce.common.aop.aspect

import com.highv.ecommerce.common.aop.annotation_class.RedissonLock
import com.highv.ecommerce.common.dto.DefaultResponse
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.redisson.api.RedissonClient
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

    private val parser: SpelExpressionParser = SpelExpressionParser()

    @Around("@annotation(com.highv.ecommerce.common.aop.annotation_class.RedissonLock)")
    fun redissonLock(joinPoint: ProceedingJoinPoint): Any?{

        val signature: MethodSignature = joinPoint.signature as MethodSignature
        val method: Method = signature.method
        val annotation: RedissonLock = method.getAnnotation(RedissonLock::class.java)
        val lockKey = parseLockKey(annotation.value, method, joinPoint.args)

        val lock = redissonClient.getLock(lockKey)

        var response = DefaultResponse("쿠폰 지급 중")

        kotlin.runCatching {

            val lockable = lock.tryLock(annotation.waitTime, annotation.leaseTime, TimeUnit.MILLISECONDS)

            if(!lockable) return@runCatching

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
}
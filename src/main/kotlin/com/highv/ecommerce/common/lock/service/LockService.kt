package com.highv.ecommerce.common.lock.service

import com.highv.ecommerce.common.lock.entity.LockKey
import com.highv.ecommerce.common.lock.repository.LockKeyRepository
import org.redisson.api.RLock
import org.redisson.api.RedissonClient
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.TimeUnit

@Service
class LockService(
    private val lockKeyRepository: LockKeyRepository,
    private val redissonClient: RedissonClient,
){

    val log = LoggerFactory.getLogger("동시성 이슈")

    private final val timeToLiveMilliseconds = 10000L

    @Transactional
    fun mySqlLock(code: String): LockKey{

        return if(!lockKeyRepository.existsByCode(code)){
            lockKeyRepository.saveAndFlush(
                LockKey(
                    code = code,
                    lockNumber = 0
                )
            )
            lockKeyRepository.findByCode(code)
        }else{
            lockKeyRepository.findByCode(code)
        }
    }

    fun deleteLock(lockKey: LockKey){
        lockKeyRepository.delete(lockKey)
    }

    fun <T> runExclusiveWithRedissonLock(lockKey: String, func: () -> T): T {
        val lock: RLock = redissonClient.getFairLock(lockKey)
        return kotlin.runCatching {
            if (lock.tryLock(20, (timeToLiveMilliseconds / 1000), TimeUnit.SECONDS)) {
                log.info("제발 들어가라 좀")
                func.invoke()
            } else throw RuntimeException("Request timed out")
        }
            .onSuccess {
                lock.unlock()
                log.info("성공시 풀림")
            }
            .onFailure {
                lock.unlock()
                log.info("실패시 풀림")
            }
            .getOrThrow()
    }
}
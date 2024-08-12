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
        return try {
            if (lock.tryLock(50, 5, TimeUnit.SECONDS)) {
                log.info("락 획득 성공: $lockKey")
                func.invoke()
            } else {
                throw RuntimeException("락 획득 시간 초과: $lockKey")
            }
        } catch (ex: Exception) {
            log.error("예외 발생: ${ex.message}", ex)
            throw ex
        } finally {
            if (lock.isHeldByCurrentThread) {
                lock.unlock()
                log.info("락 해제: $lockKey")
            } else {
                log.warn("락 해제 실패, 락이 현재 스레드에 의해 잡히지 않음: $lockKey")
            }
        }
    }
}
package com.highv.ecommerce.common.lock.service

import org.redisson.api.RLock
import org.redisson.api.RedissonClient
import org.springframework.stereotype.Service
import java.lang.Thread.sleep
import java.util.concurrent.TimeUnit

@Service
class RedisLockService(
    private val redissonClient: RedissonClient
) {
    private val timeToLiveSeconds = 7L

    /* Redisson Fair Lock */
    fun <T> runExclusiveWithRedissonLock(lockKey: String, waitTime: Long, func: () -> T): T {
        val lock: RLock = redissonClient.getFairLock(lockKey)
        return try {
            if (lock.tryLock(waitTime, timeToLiveSeconds, TimeUnit.SECONDS)) {
                sleep(1000)
                func.invoke()
            } else {
                throw RuntimeException("락 획득 시간 초과: $lockKey")
            }
        } catch (ex: Exception) {
            throw ex
        } finally {
            if (lock.isHeldByCurrentThread) {
                lock.unlock()
            }
        }
    }
}

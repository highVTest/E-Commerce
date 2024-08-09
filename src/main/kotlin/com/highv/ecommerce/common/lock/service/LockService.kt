package com.highv.ecommerce.common.lock.service

import com.highv.ecommerce.common.lock.entity.LockKey
import com.highv.ecommerce.common.lock.repository.LockKeyRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class LockService(
    private val lockKeyRepository: LockKeyRepository
){

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
}
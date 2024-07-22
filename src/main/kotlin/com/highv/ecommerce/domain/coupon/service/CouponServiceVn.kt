package com.highv.ecommerce.domain.coupon.service

import com.highv.ecommerce.common.aop.annotation_class.RedissonLock
import com.highv.ecommerce.common.dto.DefaultResponse
import com.highv.ecommerce.domain.buyer.repository.BuyerRepository
import com.highv.ecommerce.domain.coupon.entity.CouponToBuyer
import com.highv.ecommerce.domain.coupon.repository.CouponRepository
import com.highv.ecommerce.domain.coupon.repository.CouponToBuyerRepository
import com.highv.ecommerce.infra.security.UserPrincipal
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.locks.ReentrantLock

@Service
class CouponServiceVn(
    private val couponRepository: CouponRepository,
    private val couponToBuyerRepository: CouponToBuyerRepository,
    private val buyerRepository: BuyerRepository
) {
    private val lock = Any()

    //리 엔트리 락
    private val reentrantLock = ReentrantLock()

    @Transactional
    fun issuedCouponV2(couponId: Long, userPrincipal: UserPrincipal): DefaultResponse {

        synchronized(lock) {
            val buyer = buyerRepository.findByEmail(userPrincipal.email) ?: throw RuntimeException("바이어가 존재 하지 않습니다")

            kotlin.runCatching {

                val coupon = couponRepository.findByIdOrNull(couponId) ?: throw RuntimeException("쿠폰이 존재 하지 않습니다")

                if (couponToBuyerRepository.existsByCouponIdAndBuyerId(couponId, buyer.id!!)) throw RuntimeException("동일한 쿠폰은 지급 받을 수 없습니다")



                coupon.validExpiredAt()

                couponToBuyerRepository.save(
                    CouponToBuyer(
                        buyer = buyer,
                        coupon = coupon
                    )
                )

                coupon.spendCoupon()

                couponRepository.save(coupon)
            }.onFailure {
                throw RuntimeException("다시 시도 해주세요 ${it.message}")
            }
        }

        return DefaultResponse.from("쿠폰이 지급 되었습니다")
    }

    @Transactional
    fun issuedCouponV3(couponId: Long, userPrincipal: UserPrincipal): DefaultResponse {

        reentrantLock.lock()

        val buyer = buyerRepository.findByEmail(userPrincipal.email) ?: throw RuntimeException("바이어가 존재 하지 않습니다")

        if (reentrantLock.tryLock()) {
            //try - catch - finally
            kotlin.runCatching {
                val coupon = couponRepository.findByIdOrNull(couponId) ?: throw RuntimeException("쿠폰이 존재 하지 않습니다")

                if (couponToBuyerRepository.existsByCouponIdAndBuyerId(couponId, buyer.id!!)) throw RuntimeException("동일한 쿠폰은 지급 받을 수 없습니다")



                coupon.validExpiredAt()

                couponToBuyerRepository.save(
                    CouponToBuyer(
                        buyer = buyer,
                        coupon = coupon
                    )
                )

                coupon.spendCoupon()

                couponRepository.save(coupon)

            }.onFailure {
                throw RuntimeException("다시 시도해 주세요 ${it.message}")
            }.also {
                reentrantLock.unlock()
            }
        }


        return DefaultResponse.from("쿠폰이 지급 되었습니다")
    }

    @Transactional
    @RedissonLock(value = "#couponId")
    fun issuedCouponV4(couponId: Long, userPrincipal: UserPrincipal): DefaultResponse {

        val buyer = buyerRepository.findByEmail(userPrincipal.email) ?: throw RuntimeException("바이어가 존재 하지 않습니다")

        val coupon = couponRepository.findByIdOrNull(couponId) ?: throw RuntimeException("쿠폰이 존재 하지 않습니다")

        if (couponToBuyerRepository.existsByCouponIdAndBuyerId(couponId, buyer.id!!)) throw RuntimeException("동일한 쿠폰은 지급 받을 수 없습니다")



        coupon.validExpiredAt()

        couponToBuyerRepository.save(
            CouponToBuyer(
                buyer = buyer,
                coupon = coupon
            )
        )

        coupon.spendCoupon()

        couponRepository.save(coupon)



        return DefaultResponse.from("쿠폰이 지급 되었습니다")
    }
}
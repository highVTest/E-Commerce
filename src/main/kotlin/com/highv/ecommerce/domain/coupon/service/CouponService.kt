package com.highv.ecommerce.domain.coupon.service

import com.highv.ecommerce.common.dto.DefaultResponse
import com.highv.ecommerce.common.exception.*
import com.highv.ecommerce.common.innercall.TxAdvice
import com.highv.ecommerce.domain.buyer.entity.Buyer
import com.highv.ecommerce.domain.buyer.repository.BuyerRepository
import com.highv.ecommerce.domain.coupon.dto.CouponResponse
import com.highv.ecommerce.domain.coupon.dto.CreateCouponRequest
import com.highv.ecommerce.domain.coupon.dto.UpdateCouponRequest
import com.highv.ecommerce.domain.coupon.entity.Coupon
import com.highv.ecommerce.domain.coupon.entity.CouponToBuyer
import com.highv.ecommerce.domain.coupon.enumClass.DiscountPolicy
import com.highv.ecommerce.domain.coupon.repository.CouponRepository
import com.highv.ecommerce.domain.coupon.repository.CouponToBuyerRepository
import com.highv.ecommerce.domain.product.repository.ProductRepository
import org.redisson.api.RLock
import org.redisson.api.RedissonClient
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.repository.findByIdOrNull
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

@Service
class CouponService(
    private val couponRepository: CouponRepository,
    private val productRepository: ProductRepository,
    private val couponToBuyerRepository: CouponToBuyerRepository,
    private val buyerRepository: BuyerRepository,
    private val txAdvice: TxAdvice,
    private val redissonClient: RedissonClient,
    private val redisTemplate: RedisTemplate<String, String>
) {

    @Transactional
    fun createCoupon(couponRequest: CreateCouponRequest, sellerId: Long): DefaultResponse {

        if (couponRequest.discountPolicy == DiscountPolicy.DISCOUNT_RATE && couponRequest.discount > 40)
            throw InvalidCouponDiscountException(400, "할인율은 40%를 넘길 수 없습니다")

        val product = productRepository.findByIdOrNull(couponRequest.productId) ?: throw ProductNotFoundException(404, "상품이 존재하지 않습니다")

        if(product.shop.sellerId != sellerId) throw RuntimeException("다른 사용자는 해당 쿠폰을 생성 할 수 없습니다")

        if (couponRepository.existsByProductId(couponRequest.productId)) throw CouponAlreadyExistsException(400, "이미 해당 상품에 쿠폰이 발급되어 있습니다")

        couponRepository.save(
            Coupon(
                product = product,
                discountPolicy = couponRequest.discountPolicy,
                discount = couponRequest.discount,
                expiredAt = couponRequest.expiredAt,
                createdAt = LocalDateTime.now(),
                quantity = couponRequest.quantity,
                sellerId = sellerId,
                couponName = couponRequest.couponName,
            )
        )

        return DefaultResponse.from("쿠폰 생성이 완료 되었습니다")
    }

    @Transactional
    fun updateCoupon(couponId: Long, updateCouponRequest: UpdateCouponRequest, sellerId: Long): DefaultResponse {

        val result = couponRepository.findByIdOrNull(couponId) ?: throw CouponNotFoundException(404, "쿠폰이 존재하지 않습니다")

        if (result.sellerId != sellerId) throw UnauthorizedUserException(401, "다른 사용자는 해당 쿠폰을 수정할 수 없습니다")

        result.update(updateCouponRequest)

        return DefaultResponse.from("쿠폰 업데이트가 완료 되었습니다")
    }

    @Scheduled(cron = "0 0 0 * * *")
    fun deleteCoupon() {
        couponToBuyerRepository.deleteAllByExpiredAt()
        couponRepository.deleteAllByExpiredAt()
    }


    fun getSellerCouponById(couponId: Long, sellerId: Long): CouponResponse {

        val coupon = couponRepository.findByIdAndSellerId(couponId, sellerId) ?: throw CouponNotFoundException(404, "쿠폰이 존재하지 않습니다")

        return CouponResponse.from(coupon)
    }

    fun getSellerCouponList(sellerId: Long): List<CouponResponse> {
        return couponRepository.findAllBySellerId(sellerId).map { CouponResponse.from(it) }
    }

    fun getBuyerCouponById(productId: Long, buyerId: Long): CouponResponse {

        val result =
            couponToBuyerRepository.findByProductIdAndBuyerId(productId, buyerId) ?: throw CouponNotFoundException(404, "쿠폰을 가지고 있지 않습니다")

        return CouponResponse.from(result.coupon)
    }

    fun getBuyerCouponList(buyerId: Long): List<CouponResponse>? {

        return couponToBuyerRepository.findAllProductIdWithBuyerId(buyerId).let {
            couponRepository.findAllCouponIdWithBuyer(it).map { i -> CouponResponse.from(i) }
        }
    }

    //낙관적 락의 장점
    fun issuedCoupon(couponId: Long, buyerId: Long): DefaultResponse {

        kotlin.runCatching {
            val lock : RLock = redissonClient.getFairLock(createCouponLockKey(couponId))

            // 바꿔야 하는 로직은 락 안에서 실행 해야함
            // 잠금이 시도될 경우 기다 리는 시간 , 잠금이 유지 되는 시간, 시간의 단위
            if(lock.tryLock(20, 2, TimeUnit.SECONDS)) {

                val buyer = buyerRepository.findByIdOrNull(buyerId) ?: throw BuyerNotFoundException(404, "바이어가 존재하지 않습니다")



                if (couponToBuyerRepository.existsByCouponIdAndBuyerId(couponId, buyer.id!!)) throw DuplicateCouponException(
                    400,
                    "동일한 쿠폰은 지급 받을 수 없습니다"
                )

                val coupon = couponRepository.findByIdOrNull(couponId) ?: throw CouponNotFoundException(404, "쿠폰이 존재하지 않습니다")

                coupon.validExpiredAt()

                txAdvice.run {
                    saveCoupon(coupon, buyer)
                }

            }
            else throw CustomRuntimeException(400, "락 획득 시에 애러가 발생 하였습니다")
        }

            .onSuccess {
                redisUnLock(createCouponLockKey(couponId))
            }
            .onFailure {
                redisUnLock(createCouponLockKey(couponId))
            }
            .getOrThrow()

        return DefaultResponse.from("쿠폰 지급이 완료 되었습니다")

    }

    fun saveCoupon(coupon: Coupon, buyer: Buyer){

        coupon.spendCoupon()

        couponRepository.saveAndFlush(coupon)

        couponToBuyerRepository.save(
            CouponToBuyer(
                buyer = buyer,
                coupon = coupon,
                isUsed = false,
            )
        )
    }


    //해싱을 하는 방법
    private fun createCouponLockKey(couponId: Long): String{
        return "lock_${couponId}"
    }

    private fun redisUnLock(key: String): Boolean
            = redisTemplate.delete(key)

    fun getDetailCoupon(productId: Long): CouponResponse {

       return couponRepository.findByProductId(productId)
           .let { CouponResponse.from(it ?: throw CouponNotFoundException(409, "쿠폰이 존재하지 않습니다")) }

    }

    fun deleteBuyerCoupon(couponId: Long, buyerId: Long): DefaultResponse {

        val couponToBuyer = couponToBuyerRepository.findByCouponIdAndBuyerIdAndIsUsedFalse(couponId, buyerId) ?: throw CouponNotFoundException(409, "쿠폰이 존재하지 않습니다")

        couponToBuyerRepository.delete(couponToBuyer)

        return DefaultResponse.from("쿠폰 삭제가 완료 되었습니다")
    }

}
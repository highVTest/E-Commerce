package com.highv.ecommerce.domain.coupon.service

import com.highv.ecommerce.common.dto.DefaultResponse
import com.highv.ecommerce.domain.buyer.repository.BuyerRepository
import com.highv.ecommerce.domain.coupon.dto.CouponResponse
import com.highv.ecommerce.domain.coupon.dto.CreateCouponRequest
import com.highv.ecommerce.domain.coupon.dto.UpdateCouponRequest
import com.highv.ecommerce.domain.coupon.entity.Coupon
import com.highv.ecommerce.domain.coupon.entity.CouponToBuyer
import com.highv.ecommerce.domain.coupon.enumClass.DiscountPolicy
import com.highv.ecommerce.domain.coupon.repository.CouponRepository
import com.highv.ecommerce.domain.coupon.repository.CouponToBuyerRepository
import com.highv.ecommerce.domain.item_cart.repository.ItemCartRepository
import com.highv.ecommerce.domain.product.repository.ProductRepository
import com.highv.ecommerce.infra.security.UserPrincipal
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class CouponService(
    private val couponRepository: CouponRepository,
    private val productRepository: ProductRepository,
    private val couponToBuyerRepository: CouponToBuyerRepository,
    private val buyerRepository: BuyerRepository,
    private val itemCartRepository: ItemCartRepository
) {

    private val log = LoggerFactory.getLogger("CouponService::class.java")

    @Transactional
    fun createCoupon(couponRequest: CreateCouponRequest, userPrincipal: UserPrincipal): DefaultResponse {

        if (couponRequest.discountPolicy == DiscountPolicy.DISCOUNT_RATE && couponRequest.discount > 100)
            throw RuntimeException("할인율은 100%를 넘길 수 없습 니다")

        val product = productRepository.findByIdOrNull(couponRequest.productId) ?: throw RuntimeException()

        if (couponRepository.existsByProductId(couponRequest.productId)) throw RuntimeException()

        couponRepository.save(
            Coupon(
                product = product,
                discountPolicy = couponRequest.discountPolicy,
                discount = couponRequest.discount,
                expiredAt = couponRequest.expiredAt,
                createdAt = LocalDateTime.now(),
                quantity = couponRequest.quantity,
                sellerId = userPrincipal.id
            )
        )

        return DefaultResponse.from("쿠폰 생성이 완료 되었습니다")
    }

    @Transactional
    fun updateCoupon(couponId: Long, updateCouponRequest: UpdateCouponRequest, userPrincipal: UserPrincipal): DefaultResponse {

        val result = couponRepository.findByIdOrNull(couponId) ?: throw RuntimeException("쿠폰이 존재 하지 않습니다")

        if (result.sellerId != userPrincipal.id) throw RuntimeException("다른 사용자는 해당 쿠폰을 수정할 수 없습니다")

        result.update(updateCouponRequest)

        return DefaultResponse.from("쿠폰 업데이트가 완료 되었습니다")
    }

    @Transactional
    fun deleteCoupon(couponId: Long, userPrincipal: UserPrincipal): DefaultResponse {

        val result = couponRepository.findByIdOrNull(couponId) ?: throw RuntimeException("쿠폰이 존재 하지 않습니다")

        if (result.sellerId != userPrincipal.id) throw RuntimeException("다른 사용자는 해당 쿠폰을 삭제할 수 없습니다")

        couponRepository.delete(result)

        return DefaultResponse.from("쿠폰 삭제가 완료 되었습니다")
    }


    fun getSellerCouponById(couponId: Long, userPrincipal: UserPrincipal): CouponResponse {

        val coupon = couponRepository.findByIdAndSellerId(couponId, userPrincipal.id) ?: throw RuntimeException("쿠폰이 존재 하지 않습니다")

        return CouponResponse.from(coupon)
    }

    fun getSellerCouponList(userPrincipal: UserPrincipal): List<CouponResponse> {
        return couponRepository.findAllBySellerId(userPrincipal.id).map { CouponResponse.from(it) }
    }

    fun getBuyerCouponById(couponId: Long, userPrincipal: UserPrincipal): CouponResponse {

        val result =
            couponToBuyerRepository.findByCouponIdAndBuyerId(couponId, userPrincipal.id) ?: throw RuntimeException("쿠폰을 가지고 있지 않습 니다")

        return CouponResponse.from(result.coupon)
    }

    fun getBuyerCouponList(userPrincipal: UserPrincipal): List<CouponResponse>? {

        return couponToBuyerRepository.findAllProductIdWithBuyerId(userPrincipal.id).let {
            couponRepository.findAllCouponIdWithBuyer(it).map { i -> CouponResponse.from(i) }
        }
    }

    @Transactional
    fun issuedCoupon(couponId: Long, userPrincipal: UserPrincipal): DefaultResponse {

        val getLock = couponRepository.getLock("lock_$couponId", 10) == 1

        if (!getLock) throw RuntimeException("락이 걸려있지 않습니다")

        val buyer = buyerRepository.findByEmail(userPrincipal.email) ?: throw RuntimeException("바이어가 존재 하지 않습니다")

        kotlin.runCatching {
            val coupon = couponRepository.findByIdOrNull(couponId) ?: throw RuntimeException("쿠폰이 존재 하지 않습니다")

            if (couponToBuyerRepository.existsByCouponIdAndBuyerId(couponId, buyer.id!!)) throw RuntimeException("동일한 쿠폰은 지급 받을 수 없습니다")



            coupon.validExpiredAt()

            couponToBuyerRepository.save(
                CouponToBuyer(
                    buyer = buyer,
                    coupon = coupon,
                    isUsed = false,
                )
            )

            coupon.spendCoupon()

            couponRepository.save(coupon)
        }.onFailure {
            throw RuntimeException()
        }.also {
            couponRepository.releaseLock("lock_$couponId")
        }


        return DefaultResponse.from("쿠폰이 지급 되었습니다")
    }

    fun applyCoupon(couponId: Long, buyerId: Long): DefaultResponse {

        val coupon = couponToBuyerRepository.findByCouponIdAndBuyerId(couponId, buyerId) ?: throw RuntimeException("쿠폰 정보가 존재 하지 않습니다")

        log.info(coupon.coupon.product.id!!.toString())

        val itemCart = itemCartRepository.findByProductIdAndBuyerId(coupon.coupon.product.id!!, buyerId)
            ?: throw RuntimeException("장바구니에 아이템이 존재하지 않습니다")


        return DefaultResponse.from("쿠폰 적용이 완료 되었습니다")
    }

}
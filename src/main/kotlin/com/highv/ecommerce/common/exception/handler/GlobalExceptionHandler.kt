package com.highv.ecommerce.common.exception.handler

import com.highv.ecommerce.common.exception.*
import com.highv.ecommerce.common.exception.dto.ErrorResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    // 모든 RuntimeException을 처리하는 핸들러
    @ExceptionHandler(RuntimeException::class)
    fun allException(e: RuntimeException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse(409, e.message ?: "Unknown error occurred"))
    }

    // LoginException을 처리하는 핸들러
    @ExceptionHandler(LoginException::class)
    fun loginExceptionHandler(e: LoginException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponse(400, e.message))
    }

    // CustomRuntimeException을 처리하는 핸들러
    @ExceptionHandler(CustomRuntimeException::class)
    fun customRuntimeExceptionHandler(e: CustomRuntimeException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse(e.errorCode, e.message ?: "Unknown error occurred"))
    }

    // SellerNotFoundException을 처리하는 핸들러
    @ExceptionHandler(SellerNotFoundException::class)
    fun sellerNotFoundExceptionHandler(e: SellerNotFoundException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorResponse(e.errorCode, e.message))
    }

    // ProductNotFoundException을 처리하는 핸들러
    @ExceptionHandler(ProductNotFoundException::class)
    fun productNotFoundExceptionHandler(e: ProductNotFoundException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorResponse(e.errorCode, e.message))
    }

    // BuyerNotFoundException을 처리하는 핸들러
    @ExceptionHandler(BuyerNotFoundException::class)
    fun buyerNotFoundExceptionHandler(e: BuyerNotFoundException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorResponse(e.errorCode, e.message))
    }

    // BlackListNotFoundException을 처리하는 핸들러
    @ExceptionHandler(BlackListNotFoundException::class)
    fun blackListNotFoundExceptionHandler(e: BlackListNotFoundException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorResponse(e.errorCode, e.message))
    }

    // ValidationException을 처리하는 핸들러
    @ExceptionHandler(ValidationException::class)
    fun validationExceptionHandler(e: ValidationException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponse(e.errorCode, e.message))
    }

    // EmailAlreadyExistsException을 처리하는 핸들러
    @ExceptionHandler(EmailAlreadyExistsException::class)
    fun emailAlreadyExistsExceptionHandler(e: EmailAlreadyExistsException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ErrorResponse(e.errorCode, e.message))
    }

    // EmailNotFoundException을 처리하는 핸들러
    @ExceptionHandler(EmailNotFoundException::class)
    fun emailNotFoundExceptionHandler(e: EmailNotFoundException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorResponse(e.errorCode, e.message))
    }

    // InvalidAuthCodeException을 처리하는 핸들러
    @ExceptionHandler(InvalidAuthCodeException::class)
    fun invalidAuthCodeExceptionHandler(e: InvalidAuthCodeException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponse(e.errorCode, e.message))
    }

    // SellerLoginFailedException을 처리하는 핸들러
    @ExceptionHandler(SellerLoginFailedException::class)
    fun sellerLoginFailedExceptionHandler(e: SellerLoginFailedException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ErrorResponse(e.errorCode, e.message))
    }

    // BuyerLoginFailedException을 처리하는 핸들러
    @ExceptionHandler(BuyerLoginFailedException::class)
    fun buyerLoginFailedExceptionHandler(e: BuyerLoginFailedException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ErrorResponse(e.errorCode, e.message))
    }

    // KakaoAccessTokenException을 처리하는 핸들러
    @ExceptionHandler(KakaoAccessTokenException::class)
    fun kakaoAccessTokenExceptionHandler(e: KakaoAccessTokenException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorResponse(e.errorCode, e.message))
    }

    // KakaoUserInfoException을 처리하는 핸들러
    @ExceptionHandler(KakaoUserInfoException::class)
    fun kakaoUserInfoExceptionHandler(e: KakaoUserInfoException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorResponse(e.errorCode, e.message))
    }

    // OldPasswordNotMatchedException을 처리하는 핸들러
    @ExceptionHandler(OldPasswordNotMatchedException::class)
    fun oldPasswordNotMatchedExceptionHandler(e: OldPasswordNotMatchedException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponse(e.errorCode, e.message))
    }

    // InvalidRequestException을 처리하는 핸들러
    @ExceptionHandler(InvalidRequestException::class)
    fun invalidRequestExceptionHandler(e: InvalidRequestException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponse(e.errorCode, e.message))
    }

    // EmailNotVerifiedException을 처리하는 핸들러
    @ExceptionHandler(EmailNotVerifiedException::class)
    fun emailNotVerifiedExceptionHandler(e: EmailNotVerifiedException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorResponse(e.errorCode, e.message))
    }

    // UnauthorizedEmailException을 처리하는 핸들러
    @ExceptionHandler(UnauthorizedEmailException::class)
    fun unauthorizedEmailExceptionHandler(e: UnauthorizedEmailException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponse(e.errorCode, e.message))
    }

    // SocialLoginException을 처리하는 핸들러
    @ExceptionHandler(SocialLoginException::class)
    fun socialLoginExceptionHandler(e: SocialLoginException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponse(e.errorCode, e.message))
    }

    // PasswordMismatchException을 처리하는 핸들러
    @ExceptionHandler(PasswordMismatchException::class)
    fun passwordMismatchExceptionHandler(e: PasswordMismatchException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponse(e.errorCode, e.message))
    }

    // DuplicatePasswordException을 처리하는 핸들러
    @ExceptionHandler(DuplicatePasswordException::class)
    fun duplicatePasswordExceptionHandler(e: DuplicatePasswordException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponse(e.errorCode, e.message))
    }

    // UnauthorizedUserException을 처리하는 핸들러
    @ExceptionHandler(UnauthorizedUserException::class)
    fun unauthorizedUserExceptionHandler(e: UnauthorizedUserException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ErrorResponse(e.errorCode, e.message))
    }

    // InvalidCouponRequestException을 처리하는 핸들러
    @ExceptionHandler(InvalidCouponRequestException::class)
    fun invalidCouponRequestExceptionHandler(e: InvalidCouponRequestException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponse(e.errorCode, e.message))
    }

    // InvalidDiscountPolicyException을 처리하는 핸들러
    @ExceptionHandler(InvalidDiscountPolicyException::class)
    fun invalidDiscountPolicyExceptionHandler(e: InvalidDiscountPolicyException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponse(e.errorCode, e.message))
    }

    // CouponSoldOutException을 처리하는 핸들러
    @ExceptionHandler(CouponSoldOutException::class)
    fun couponSoldOutExceptionHandler(e: CouponSoldOutException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponse(e.errorCode, e.message))
    }

    // CouponExpiredException을 처리하는 핸들러
    @ExceptionHandler(CouponExpiredException::class)
    fun couponExpiredExceptionHandler(e: CouponExpiredException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponse(e.errorCode, e.message))
    }

    // InvalidCouponDiscountException을 처리하는 핸들러
    @ExceptionHandler(InvalidCouponDiscountException::class)
    fun invalidCouponDiscountExceptionHandler(e: InvalidCouponDiscountException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponse(e.errorCode, e.message))
    }

    // CouponAlreadyExistsException을 처리하는 핸들러
    @ExceptionHandler(CouponAlreadyExistsException::class)
    fun couponAlreadyExistsExceptionHandler(e: CouponAlreadyExistsException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponse(e.errorCode, e.message))
    }

    // CouponNotFoundException을 처리하는 핸들러
    @ExceptionHandler(CouponNotFoundException::class)
    fun couponNotFoundExceptionHandler(e: CouponNotFoundException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorResponse(e.errorCode, e.message))
    }

    // CouponDistributionException을 처리하는 핸들러
    @ExceptionHandler(CouponDistributionException::class)
    fun couponDistributionExceptionHandler(e: CouponDistributionException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorResponse(e.errorCode, e.message))
    }

    // ItemNotFoundException을 처리하는 핸들러
    @ExceptionHandler(ItemNotFoundException::class)
    fun itemNotFoundExceptionHandler(e: ItemNotFoundException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorResponse(e.errorCode, e.message))
    }

    // DuplicateCouponException을 처리하는 핸들러
    @ExceptionHandler(DuplicateCouponException::class)
    fun duplicateCouponExceptionHandler(e: DuplicateCouponException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponse(e.errorCode, e.message))
    }

    // InvalidQuantityException을 처리하는 핸들러
    @ExceptionHandler(InvalidQuantityException::class)
    fun invalidQuantityExceptionHandler(e: InvalidQuantityException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponse(e.errorCode, e.message))
    }

    @ExceptionHandler(CartEmptyException::class)
    fun cartEmptyExceptionHandler(e: CartEmptyException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponse(e.errorCode, e.message))
    }

    @ExceptionHandler(InsufficientStockException::class)
    fun insufficientStockExceptionHandler(e: InsufficientStockException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponse(e.errorCode, e.message))
    }

    // UnauthorizedException을 처리하는 핸들러
    @ExceptionHandler(UnauthorizedException::class)
    fun unauthorizedExceptionHandler(e: UnauthorizedException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ErrorResponse(e.errorCode, e.message))
    }

    // ReviewNotFoundException을 처리하는 핸들러
    @ExceptionHandler(ReviewNotFoundException::class)
    fun reviewNotFoundExceptionHandler(e: ReviewNotFoundException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorResponse(e.errorCode, e.message))
    }

    // ShopNotFoundException을 처리하는 핸들러
    @ExceptionHandler(ShopNotFoundException::class)
    fun shopNotFoundExceptionHandler(e: ShopNotFoundException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorResponse(e.errorCode, e.message))
    }

    // EmailVerificationNotFoundException을 처리하는 핸들러
    @ExceptionHandler(EmailVerificationNotFoundException::class)
    fun emailVerificationNotFoundExceptionHandler(e: EmailVerificationNotFoundException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorResponse(e.errorCode, e.message))
    }

    // UnverifiedEmailException을 처리하는 핸들러
    @ExceptionHandler(UnverifiedEmailException::class)
    fun unverifiedEmailExceptionHandler(e: UnverifiedEmailException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponse(e.errorCode, e.message))
    }

    // ShopAlreadyExistsException을 처리하는 핸들러
    @ExceptionHandler(ShopAlreadyExistsException::class)
    fun shopAlreadyExistsExceptionHandler(e: ShopAlreadyExistsException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ErrorResponse(e.errorCode, e.message))
    }

    @ExceptionHandler(ModelNotFoundException::class)
    fun modelNotFoundException(e: ModelNotFoundException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ErrorResponse(e.errorCode, e.message))
    }
}

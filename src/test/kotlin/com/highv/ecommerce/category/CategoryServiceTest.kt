//package com.highv.ecommerce.category
//
//import com.highv.ecommerce.domain.product.category.entity.Category
//import com.highv.ecommerce.domain.product.category.repository.CategoryRepository
//import com.highv.ecommerce.domain.product.category.service.CategoryService
//import io.kotest.assertions.throwables.shouldThrow
//import io.kotest.core.spec.style.BehaviorSpec
//import io.kotest.matchers.shouldBe
//import io.mockk.clearAllMocks
//import io.mockk.every
//import io.mockk.mockk
//import io.mockk.slot
//import io.mockk.verify
//import jakarta.persistence.EntityNotFoundException
//import org.springframework.data.repository.findByIdOrNull
//
//class CategoryServiceTest :BehaviorSpec({
//    val categoryRepository = mockk<CategoryRepository>()
//    val categoryService = CategoryService(categoryRepository)
//
//    Given("addCategory 를 실행할 때"){
//        When("유효한 카테고리 이름이 제공된 경우") {
//            val categoryName = "Food"
//            val categorySlot = slot<Category>()
//            every { categoryRepository.save(capture(categorySlot)) } answers {
//                categorySlot.captured.apply { id = 1L }
//            }
//
//            Then("카테고리가 추가된다") {
//                val response = categoryService.addCategory(categoryName)
//                response.name shouldBe categoryName
//                response.id shouldBe 1L
//                verify(exactly = 1) { categoryRepository.save(any<Category>()) }
//            }
//        }
//    }
//
//    Given("deleteCategory 를 실행할 때"){
//        When("유효한 카테고리 ID가 제공된 경우") {
//            val categoryId = 1L
//            every { categoryRepository.findByIdOrNull(categoryId) } returns Category(name = "Food").apply{id=1L}
//            every { categoryRepository.delete(any()) } returns Unit
//
//            Then("카테고리가 성공적으로 삭제된다") {
//                categoryService.deleteCategory(categoryId)
//                verify(exactly = 1) { categoryRepository.findByIdOrNull(categoryId) }
//                verify(exactly = 1) { categoryRepository.delete(any<Category>()) }
//            }
//        }
//
//        When("유효하지 않은 카테고리 ID가 제공된 경우") {
//            val categoryId = 999L
//            every { categoryRepository.findByIdOrNull(categoryId) } returns null
//
//            Then("EntityNotFoundException 이 발생한다") {
//                shouldThrow<EntityNotFoundException> {
//                    categoryService.deleteCategory(categoryId)
//                }
//                verify(exactly = 1) { categoryRepository.findByIdOrNull(categoryId) }
//                verify(exactly = 0) { categoryRepository.delete(any<Category>()) }
//            }
//        }
//    }
//
//    afterEach {
//        clearAllMocks()
//    }
//})
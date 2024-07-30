package com.highv.ecommerce.domain.product.service

import com.highv.ecommerce.domain.favorite.service.FavoriteService
import com.highv.ecommerce.domain.product.dto.ProductResponse
import com.highv.ecommerce.domain.product.dto.TopSearchKeyword
import com.highv.ecommerce.domain.product.repository.ProductRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.redis.core.HashOperations
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ZSetOperations
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class ProductSearchService(
    private val redisTemplate: RedisTemplate<String, String>,
    private val redisTemplateForProductSearch: RedisTemplate<String, Page<ProductResponse>>,
    private val productRepository: ProductRepository,
    private val favoriteService: FavoriteService
) {
    private val topSearchZSet: ZSetOperations<String, String> = redisTemplate.opsForZSet()
    private val searchHash: HashOperations<String, String, Page<ProductResponse>> =
        redisTemplateForProductSearch.opsForHash()

    fun topSearch10(limit: Long): Set<TopSearchKeyword> {
        val rangeWithScores = topSearchZSet.reverseRangeWithScores("topSearch", 0, limit - 1)
        if (rangeWithScores != null) {
            return rangeWithScores.map { TopSearchKeyword(it.value, it.score) }.toSet()
        }
        return emptySet()
    }

    fun searchByRedis(keyword: String, pageRequest: PageRequest): Page<ProductResponse> {
        addTermTopSearch(keyword)

        val sortProperty = pageRequest.sort.iterator().next().property
        val sortDirection =
            if (pageRequest.sort.isSorted && pageRequest.sort.getOrderFor(sortProperty)?.isAscending == true) "ASC" else "DESC"
        val cacheKey = createCacheKey(keyword, sortProperty, sortDirection)

        val cachedData = searchHash.get("searchList", cacheKey)
        if (cachedData != null) {
            return cachedData
        } else {
            cacheAllFilterCases(keyword, pageRequest)
            val productInfo = productRepository.searchByKeywordPaginated(keyword, pageRequest)
            if (productInfo.hasContent()) {
                addTermSearch(
                    cacheKey,
                    productInfo.map { ProductResponse.from(it, favoriteService.countFavorite(it.id!!)) })
                return productInfo.map { ProductResponse.from(it, favoriteService.countFavorite(it.id!!)) }
            }
        }
        return Page.empty(pageRequest)
    }

    private fun addTermTopSearch(term: String) {
        topSearchZSet.incrementScore("topSearch", term, 1.0)
        redisTemplate.expire(term, 10, TimeUnit.MINUTES)
    }

    private fun addTermSearch(term: String, showInfos: Page<ProductResponse>) {
        searchHash.put("searchList", term, showInfos)
        redisTemplateForProductSearch.expire(term, 10, TimeUnit.MINUTES)
    }

    private fun createCacheKey(keyword: String, sortProperty: String, sortDirection: String): String {
        return "searchList:$keyword:$sortProperty:$sortDirection"
    }

    private fun cacheAllFilterCases(keyword: String, pageRequest: PageRequest) {
        val filterCases = listOf("price", "createdAt")
        val directions = listOf("ASC", "DESC")
        for (filterCase in filterCases) {
            for (direction in directions) {
                val sort = if (direction == "ASC") Sort.by(filterCase).ascending() else Sort.by(filterCase).descending()
                val pageRequestWithSort = PageRequest.of(0, pageRequest.pageSize, sort)
                val productInfo = productRepository.searchByKeywordPaginated(keyword, pageRequestWithSort)
                if (productInfo.hasContent()) {
                    val cacheKey = createCacheKey(keyword, filterCase, direction)
                    addTermSearch(
                        cacheKey,
                        productInfo.map { ProductResponse.from(it, favoriteService.countFavorite(it.id!!)) })
                }
            }
        }
    }
}

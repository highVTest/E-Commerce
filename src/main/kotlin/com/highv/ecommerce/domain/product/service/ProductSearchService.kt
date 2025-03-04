package com.highv.ecommerce.domain.product.service

import com.highv.ecommerce.domain.favorite.dto.FavoriteCount
import com.highv.ecommerce.domain.favorite.service.FavoriteService
import com.highv.ecommerce.domain.product.dto.ProductSummaryResponse
import com.highv.ecommerce.domain.product.dto.TopSearchKeyword
import com.highv.ecommerce.domain.product.repository.ProductRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.redis.core.HashOperations
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ZSetOperations
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class ProductSearchService(
    private val redisTemplate: RedisTemplate<String, String>,
    private val redisTemplateForProductSearch: RedisTemplate<String, Page<ProductSummaryResponse>>,
    private val productRepository: ProductRepository,
    private val favoriteService: FavoriteService
) {
    private val topSearchZSet: ZSetOperations<String, String> = redisTemplate.opsForZSet()
    private val searchHash: HashOperations<String, String, Page<ProductSummaryResponse>> =
        redisTemplateForProductSearch.opsForHash()

    fun topSearch10(limit: Long): Set<TopSearchKeyword> {
        val rangeWithScores = topSearchZSet.reverseRangeWithScores("topSearch", 0, limit - 1)
        if (rangeWithScores != null) {
            return rangeWithScores.map { TopSearchKeyword(it.value, it.score) }.toSet()
        }
        return emptySet()
    }

    fun searchByRedis(keyword: String, pageRequest: PageRequest): Page<ProductSummaryResponse> {
        addTermTopSearch(keyword)

        val sortProperty = pageRequest.sort.iterator().next().property
        val sortDirection =
            if (pageRequest.sort.isSorted && pageRequest.sort.getOrderFor(sortProperty)?.isAscending == true) "ASC" else "DESC"
        val pageNumber = pageRequest.pageNumber

        if (pageNumber == 0) {
            val cacheKey = createCacheKey(keyword, sortProperty, sortDirection)

            val cachedData = searchHash.get("searchList", cacheKey)
            if (cachedData != null) {
                addTermSearch(cacheKey, cachedData)
                return cachedData
            } else {
                cacheAllFilterCases(keyword, pageRequest)
                return searchHash.get("searchList", cacheKey) ?: Page.empty(pageRequest)
            }
        } else {
            val productInfo = productRepository.searchByKeywordPaginated(keyword, pageRequest)
            val likeCount = favoriteCountMapping(productInfo.map { it.id }.toList())
            return productInfo.map { ProductSummaryResponse.from(it, likeCount[it.id]?.count ?: 0) }
        }
    }

    fun addTermTopSearch(term: String) {
        topSearchZSet.incrementScore("topSearch", term, 1.0)
        redisTemplate.expire("topSearch", 10, TimeUnit.MINUTES)
    }

    fun addTermSearch(term: String, showInfos: Page<ProductSummaryResponse>) {
        searchHash.put("searchList", term, showInfos)
        redisTemplateForProductSearch.expire("searchList", 10, TimeUnit.MINUTES)
    }

    fun createCacheKey(keyword: String, sortProperty: String, sortDirection: String): String {
        return "searchList:$keyword:$sortProperty:$sortDirection"
    }

    fun cacheAllFilterCases(keyword: String, pageRequest: PageRequest) {
        val filterCases = listOf("price", "createdAt")
        val directions = listOf("ASC", "DESC")

        for (filterCase in filterCases) {
            for (direction in directions) {

                val sort = if (direction == "ASC") Sort.by(filterCase).ascending() else Sort.by(filterCase).descending()
                val pageRequestWithSort = PageRequest.of(0, pageRequest.pageSize, sort)
                val productInfo = productRepository.searchByKeywordPaginated(keyword, pageRequestWithSort)

                val likeCount = favoriteCountMapping(productInfo.map { it.id }.toList())

                if (productInfo.hasContent()) {
                    val cacheKey = createCacheKey(keyword, filterCase, direction)
                    addTermSearch(
                        cacheKey,
                        productInfo.map { ProductSummaryResponse.from(it, likeCount[it.id]?.count ?: 0) })
                }
            }
        }
    }

    fun getAllProducts(pageable: Pageable): Page<ProductSummaryResponse> {

        val sortProperty = pageable.sort.iterator().next().property
        val sortDirection =
            if (pageable.sort.isSorted && pageable.sort.getOrderFor(sortProperty)?.isAscending == true) "ASC" else "DESC"

        val pageNumber = pageable.pageNumber

        if (pageNumber == 0) {
            val cacheKey = createCacheKey("all", sortProperty, sortDirection)
            val cachedData = searchHash.get("productList", cacheKey)

            if (cachedData != null) {
                return cachedData
            } else {
                val products = productRepository.findAllPaginated(pageable)

                val likeCount = favoriteCountMapping(products.map { it.id }.toList())

                searchHash.put(
                    "productList",
                    cacheKey,
                    products.map { ProductSummaryResponse.from(it, likeCount[it.id]?.count ?: 0) }
                )

                redisTemplateForProductSearch.expire("productList", 5, TimeUnit.MINUTES)

                return searchHash.get("productList", cacheKey) ?: Page.empty(pageable)
            }
        } else {
            val products = productRepository.findAllPaginated(pageable)
            val likeCount = favoriteCountMapping(products.map { it.id }.toList())
            return products.map { ProductSummaryResponse.from(it, likeCount[it.id]?.count ?: 0) }
        }
    }

    private fun favoriteCountMapping(productIds: List<Long>): MutableMap<Long, FavoriteCount> {

        val favoritesCount = favoriteService.countFavorites(productIds)

        val likeCount: MutableMap<Long, FavoriteCount> = mutableMapOf()

        favoritesCount.forEach {
            if (!likeCount.containsKey(it.productId)) {
                likeCount[it.productId] = it
            }
        }

        return likeCount
    }
}



package com.highv.ecommerce.domain.product.repository

import com.querydsl.core.types.Order
import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.dsl.ComparableExpressionBase
import com.querydsl.core.types.dsl.Expressions
import org.springframework.data.domain.Sort
import java.time.LocalDateTime

fun Sort.Order.toOrderSpecifier(): OrderSpecifier<*> {
    val direction = if (isAscending) Order.ASC else Order.DESC
    val path = when (property) {
        "price" -> Expressions.numberPath(Double::class.java, "price")
        "likes" -> Expressions.numberPath(Int::class.java, "likes")
        "createdAt" -> Expressions.dateTimePath(LocalDateTime::class.java, "createdAt")
        else -> throw IllegalArgumentException("Unknown sort property: $property")
    }
    return OrderSpecifier(direction, path as ComparableExpressionBase<*>)
}
package com.proto.type.base

import com.proto.type.base.data.database.entity.UserEntity

class HighlightObject(
    val id: String = "",
    val range: IntRange = IntRange.EMPTY,
    val type: String = UserEntity.TAG,
    val value: String = ""
)

class HighlightParameters(
    val dataSource: List<Any>,
    val highlightedForegroundColor: Int,
    val prefix: String,
    val shouldUnderLine: Boolean = false
)
package com.wutsi.platform.contact.dto

import kotlin.Int
import kotlin.Long
import kotlin.collections.List

public data class SearchContactRequest(
    public val contactIds: List<Long> = emptyList(),
    public val limit: Int = 30,
    public val offset: Int = 0
)

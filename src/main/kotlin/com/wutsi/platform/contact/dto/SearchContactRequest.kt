package com.wutsi.platform.contact.dto

public data class SearchContactRequest(
    public val accountId: Long = 0,
    public val limit: Int = 30,
    public val offset: Int = 0
)

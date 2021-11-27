package com.wutsi.platform.contact.dto

import kotlin.collections.List

public data class SearchContactResponse(
    public val contacts: List<Contact> = emptyList()
)

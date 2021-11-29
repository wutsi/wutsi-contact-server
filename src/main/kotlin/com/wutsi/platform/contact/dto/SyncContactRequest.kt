package com.wutsi.platform.contact.dto

import kotlin.Long
import kotlin.String
import kotlin.collections.List

public data class SyncContactRequest(
    public val accountId: Long = 0,
    public val tenantId: Long = 0,
    public val phoneNumbers: List<String> = emptyList()
)

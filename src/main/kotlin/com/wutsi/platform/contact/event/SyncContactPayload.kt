package com.wutsi.platform.contact.event

data class SyncContactPayload(
    val accountId: Long = 0,
    val tenantId: Long = 0,
    val phoneNumbers: List<String> = emptyList()
)

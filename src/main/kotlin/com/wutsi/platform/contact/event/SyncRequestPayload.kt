package com.wutsi.platform.contact.event

data class SyncRequestPayload(
    val accountId: Long = -1,
    val phoneNumber: String = ""
)

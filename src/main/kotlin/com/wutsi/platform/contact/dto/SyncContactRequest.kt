package com.wutsi.platform.contact.dto

public data class SyncContactRequest(
    public val phoneNumbers: List<String> = emptyList()
)

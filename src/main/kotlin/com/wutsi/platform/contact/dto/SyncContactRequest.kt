package com.wutsi.platform.contact.dto

import kotlin.String
import kotlin.collections.List

public data class SyncContactRequest(
    public val phoneNumbers: List<String> = emptyList()
)

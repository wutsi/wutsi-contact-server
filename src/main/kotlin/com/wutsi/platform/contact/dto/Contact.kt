package com.wutsi.platform.contact.dto

import org.springframework.format.`annotation`.DateTimeFormat
import java.time.OffsetDateTime
import kotlin.Long

public data class Contact(
    public val accountId: Long = 0,
    public val contactId: Long = 0,
    @get:DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    public val created: OffsetDateTime = OffsetDateTime.now()
)

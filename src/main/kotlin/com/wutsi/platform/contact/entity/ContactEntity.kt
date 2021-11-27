package com.wutsi.platform.contact.entity

import java.time.OffsetDateTime
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "T_CONTACT")
data class ContactEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    val accountId: Long = -1,
    val contactId: Long = -1,
    val tenantId: Long = -1,
    val created: OffsetDateTime = OffsetDateTime.now(),
)

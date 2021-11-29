package com.wutsi.platform.contact.delegate

import com.wutsi.platform.contact.dto.ContactSummary
import com.wutsi.platform.contact.entity.ContactEntity

fun ContactEntity.toContact() = ContactSummary(
    contactId = this.contactId,
    accountId = this.accountId,
    created = this.created
)

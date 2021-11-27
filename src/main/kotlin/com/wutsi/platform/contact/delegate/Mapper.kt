package com.wutsi.platform.contact.delegate

import com.wutsi.platform.contact.dto.Contact
import com.wutsi.platform.contact.entity.ContactEntity

fun ContactEntity.toContact() = Contact(
    contactId = this.contactId,
    accountId = this.accountId,
    created = this.created
)

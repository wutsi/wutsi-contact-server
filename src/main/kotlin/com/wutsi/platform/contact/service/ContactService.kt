package com.wutsi.platform.contact.service

import com.wutsi.platform.contact.dao.ContactRepository
import com.wutsi.platform.contact.dto.SearchContactRequest
import com.wutsi.platform.contact.entity.ContactEntity
import com.wutsi.platform.payment.event.TransactionEventPayload
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class ContactService(
    private val dao: ContactRepository,
) {
    fun search(request: SearchContactRequest, tenantId: Long): List<ContactEntity> {
        val pageable = PageRequest.of(request.offset / request.limit, request.limit)
        return dao.findByAccountIdAndTenantId(request.accountId, tenantId, pageable)
    }

    @Transactional
    fun addContact(payload: TransactionEventPayload): ContactEntity? {
        if (payload.recipientId == null)
            return null
        return addContact(payload.userId, payload.recipientId!!, payload.tenantId)
    }

    @Transactional
    fun addContact(accountId: Long, contactId: Long, tenantId: Long): ContactEntity? {
        val opt = dao.findByAccountIdAndContactIdAndTenantId(accountId, contactId, tenantId)
        if (opt.isPresent)
            return null

        return dao.save(
            ContactEntity(
                accountId = accountId,
                contactId = contactId,
                tenantId = tenantId
            )
        )
    }
}

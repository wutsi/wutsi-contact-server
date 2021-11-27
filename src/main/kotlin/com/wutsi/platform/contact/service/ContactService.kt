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
    private val dao: ContactRepository
) {
    fun search(request: SearchContactRequest): List<ContactEntity> {
        val pageable = PageRequest.of(request.offset / request.limit, request.limit)
        return dao.findByAccountIdAndTenantId(request.accountId, request.tenantId, pageable)
    }

    @Transactional
    fun addContact(payload: TransactionEventPayload): ContactEntity? {
        if (payload.recipientId == null)
            return null

        val opt = dao.findByAccountIdAndContactIdAndTenantId(payload.userId, payload.recipientId!!, payload.tenantId)
        if (opt.isPresent)
            return null

        return dao.save(
            ContactEntity(
                accountId = payload.userId,
                contactId = payload.recipientId!!,
                tenantId = payload.tenantId
            )
        )
    }
}

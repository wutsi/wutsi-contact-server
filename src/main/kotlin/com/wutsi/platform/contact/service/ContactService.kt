package com.wutsi.platform.contact.service

import com.wutsi.platform.account.WutsiAccountApi
import com.wutsi.platform.account.dto.SearchAccountRequest
import com.wutsi.platform.contact.dao.ContactRepository
import com.wutsi.platform.contact.dto.SearchContactRequest
import com.wutsi.platform.contact.entity.ContactEntity
import com.wutsi.platform.contact.entity.PhoneEntity
import com.wutsi.platform.payment.event.TransactionEventPayload
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class ContactService(
    private val dao: ContactRepository,
    private val accountApi: WutsiAccountApi,
) {
    fun search(request: SearchContactRequest, tenantId: Long): List<ContactEntity> {
        val pageable = PageRequest.of(request.offset / request.limit, request.limit)
        return dao.findByAccountIdAndTenantId(request.accountId, tenantId, pageable)
    }

    fun addContact(payload: TransactionEventPayload): ContactEntity? {
        if (payload.recipientId == null)
            return null
        return addContact(payload.userId, payload.recipientId!!, payload.tenantId)
    }

    fun addContact(accountId: Long, contactId: Long, tenantId: Long): ContactEntity? {
        if (accountId == contactId)
            return null

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

    fun addContact(accountId: Long, tenantId: Long, phone: PhoneEntity): ContactEntity? {
        val accounts = accountApi.searchAccount(
            SearchAccountRequest(
                phoneNumber = phone.number
            )
        ).accounts
        if (accounts.isEmpty())
            return null

        return addContact(accountId, accounts[0].id, tenantId)
    }
}

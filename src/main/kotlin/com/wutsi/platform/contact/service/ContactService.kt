package com.wutsi.platform.contact.service

import com.wutsi.platform.account.WutsiAccountApi
import com.wutsi.platform.account.dto.SearchAccountRequest
import com.wutsi.platform.account.event.AccountCreatedPayload
import com.wutsi.platform.contact.dao.ContactRepository
import com.wutsi.platform.contact.dto.SearchContactRequest
import com.wutsi.platform.contact.entity.ContactEntity
import com.wutsi.platform.contact.entity.PhoneEntity
import com.wutsi.platform.payment.event.TransactionEventPayload
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class ContactService(
    private val dao: ContactRepository,
    private val accountApi: WutsiAccountApi,
    private val securityManager: SecurityManager,
    private val tenantProvider: TenantProvider,
    private val phoneService: PhoneService
) {
    fun search(request: SearchContactRequest): List<ContactEntity> {
        val pageable = PageRequest.of(request.offset / request.limit, request.limit)
        val tenantId = tenantProvider.id()
        return if (request.contactIds.isEmpty())
            dao.findByAccountIdAndTenantId(securityManager.currentUserId(), tenantId, pageable)
        else
            dao.findByAccountIdAndContactIdInAndTenantId(
                securityManager.currentUserId(),
                request.contactIds,
                tenantId,
                pageable
            )
    }

    @Transactional
    fun addContact(payload: TransactionEventPayload): ContactEntity? {
        if (payload.recipientId == null)
            return null
        return addContact(payload.accountId, payload.recipientId!!)
    }

    @Transactional
    fun addContacts(payload: AccountCreatedPayload): Int {
        val phones = phoneService.findPhones(payload.phoneNumber)
        var added = 0
        phones.forEach {
            if (addContact(it.accountId, payload.accountId) != null)
                added++
        }
        return added
    }

    @Transactional
    fun addContact(phone: PhoneEntity): ContactEntity? {
        val contacts = accountApi.searchAccount(
            request = SearchAccountRequest(
                phoneNumber = phone.number
            )
        ).accounts
        return if (contacts.isEmpty())
            null
        else
            addContact(phone.accountId, contacts[0].id)
    }

    @Transactional
    fun addContact(accountId: Long, contactId: Long): ContactEntity {
        val tenantId = tenantProvider.id()
        val opt = dao.findByAccountIdAndContactIdAndTenantId(accountId, contactId, tenantId)
        if (opt.isPresent)
            return opt.get()

        return dao.save(
            ContactEntity(
                accountId = accountId,
                contactId = contactId,
                tenantId = tenantId
            )
        )
    }
}

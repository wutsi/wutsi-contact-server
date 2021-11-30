package com.wutsi.platform.contact.`delegate`

import com.wutsi.platform.contact.dto.SyncContactRequest
import com.wutsi.platform.contact.dto.SyncContactResponse
import com.wutsi.platform.contact.service.ContactService
import com.wutsi.platform.contact.service.PhoneService
import com.wutsi.platform.contact.service.SecurityManager
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.tracing.TracingContext
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
public class SyncContactsDelegate(
    private val tracingContext: TracingContext,
    private val securityManager: SecurityManager,
    private val phoneService: PhoneService,
    private val contactService: ContactService,
    private val logger: KVLogger,
) {
    @Transactional
    public fun invoke(request: SyncContactRequest): SyncContactResponse {
        val accountId = securityManager.currentUserId()
        val tenantId = tracingContext.tenantId()!!.toLong()
        logger.add("phone_numbers", request.phoneNumbers)
        logger.add("account_id", accountId)

        request.phoneNumbers.forEach {
            sync(accountId, tenantId, it)
        }

        return SyncContactResponse(accountId = accountId)
    }

    private fun sync(accountId: Long, tenantId: Long, phoneNumber: String) {
        val phone = phoneService.addPhone(accountId, tenantId, phoneNumber)
        if (phone != null) {
            logger.add("phone_ids", phone.id)

            val contact = contactService.addContact(accountId, tenantId, phone)
            logger.add("contact_ids", contact?.id)
        }
    }
}

package com.wutsi.platform.contact.`delegate`

import com.wutsi.platform.contact.dto.CreateContactRequest
import com.wutsi.platform.contact.dto.CreateContactResponse
import com.wutsi.platform.contact.service.ContactService
import com.wutsi.platform.contact.service.SecurityManager
import com.wutsi.platform.core.logging.KVLogger
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
public class CreateContactDelegate(
    private val service: ContactService,
    private val securityManager: SecurityManager,
    private val logger: KVLogger,
) {
    @Transactional
    public fun invoke(request: CreateContactRequest): CreateContactResponse {
        val accountId = securityManager.currentUserId()
        logger.add("account_id", accountId)
        logger.add("contact_id", request.contactId)

        val contact = service.addContact(
            accountId = accountId,
            contactId = request.contactId
        )
        return CreateContactResponse(
            id = contact?.id ?: -1
        )
    }
}

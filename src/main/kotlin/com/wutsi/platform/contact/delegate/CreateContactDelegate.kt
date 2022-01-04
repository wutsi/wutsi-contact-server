package com.wutsi.platform.contact.`delegate`

import com.wutsi.platform.contact.dto.CreateContactRequest
import com.wutsi.platform.contact.dto.CreateContactResponse
import com.wutsi.platform.contact.service.ContactService
import com.wutsi.platform.contact.service.SecurityManager
import com.wutsi.platform.core.tracing.TracingContext
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
public class CreateContactDelegate(
    private val service: ContactService,
    private val securityManager: SecurityManager,
    private val tracingContext: TracingContext,
) {
    @Transactional
    public fun invoke(request: CreateContactRequest): CreateContactResponse {
        val contact = service.addContact(
            accountId = securityManager.currentUserId(),
            contactId = request.contactId,
            tenantId = tracingContext.tenantId()!!.toLong()
        )
        return CreateContactResponse(
            id = contact.id!!
        )
    }
}

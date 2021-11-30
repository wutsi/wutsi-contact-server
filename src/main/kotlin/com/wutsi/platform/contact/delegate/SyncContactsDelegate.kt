package com.wutsi.platform.contact.`delegate`

import com.wutsi.platform.contact.dto.SyncContactRequest
import com.wutsi.platform.contact.dto.SyncContactResponse
import com.wutsi.platform.contact.event.EventURN
import com.wutsi.platform.contact.event.SyncContactPayload
import com.wutsi.platform.contact.service.SecurityManager
import com.wutsi.platform.core.stream.EventStream
import com.wutsi.platform.core.tracing.TracingContext
import org.springframework.stereotype.Service

@Service
public class SyncContactsDelegate(
    private val stream: EventStream,
    private val tracingContext: TracingContext,
    private val securityManager: SecurityManager
) {
    public fun invoke(request: SyncContactRequest): SyncContactResponse {
        val accountId = securityManager.currentUserId()
        stream.enqueue(
            type = EventURN.SYNC_REQUESTED.urn,
            payload = SyncContactPayload(
                accountId = accountId,
                tenantId = tracingContext.tenantId()?.toLong() ?: -1,
                phoneNumbers = request.phoneNumbers
            )
        )
        return SyncContactResponse(accountId = accountId)
    }
}

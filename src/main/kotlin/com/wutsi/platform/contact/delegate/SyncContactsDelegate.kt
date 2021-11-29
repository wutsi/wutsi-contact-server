package com.wutsi.platform.contact.`delegate`

import com.wutsi.platform.contact.dto.SyncContactRequest
import com.wutsi.platform.contact.dto.SyncContactResponse
import com.wutsi.platform.contact.event.EventURN
import com.wutsi.platform.contact.event.SyncContactPayload
import com.wutsi.platform.core.stream.EventStream
import org.springframework.stereotype.Service

@Service
public class SyncContactsDelegate(
    private val stream: EventStream
) {
    public fun invoke(request: SyncContactRequest): SyncContactResponse {
        stream.enqueue(
            type = EventURN.SYNC_REQUESTED.urn,
            payload = SyncContactPayload(
                accountId = request.accountId,
                tenantId = request.tenantId,
                phoneNumbers = request.phoneNumbers
            )
        )
        return SyncContactResponse(accountId = request.accountId)
    }
}

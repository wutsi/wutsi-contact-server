package com.wutsi.platform.contact.`delegate`

import com.wutsi.platform.contact.dto.SyncContactRequest
import com.wutsi.platform.contact.dto.SyncContactResponse
import com.wutsi.platform.contact.event.EventURN
import com.wutsi.platform.contact.event.SyncRequestPayload
import com.wutsi.platform.contact.service.SecurityManager
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.stream.EventStream
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
public class SyncContactsDelegate(
    private val securityManager: SecurityManager,
    private val eventStream: EventStream,
    private val logger: KVLogger,
) {
    @Transactional
    public fun invoke(request: SyncContactRequest): SyncContactResponse {
        val accountId = securityManager.currentUserId()
        logger.add("account_id", accountId)
        logger.add("phone_number_count", request.phoneNumbers.size)

        request.phoneNumbers.forEach {
            eventStream.enqueue(
                EventURN.SYNC_REQUEST.urn,
                SyncRequestPayload(
                    accountId = accountId,
                    phoneNumber = it
                )
            )
        }

        return SyncContactResponse(accountId = accountId)
    }
}

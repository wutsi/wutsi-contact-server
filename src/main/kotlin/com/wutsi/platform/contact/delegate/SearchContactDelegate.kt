package com.wutsi.platform.contact.`delegate`

import com.wutsi.platform.contact.dto.SearchContactRequest
import com.wutsi.platform.contact.dto.SearchContactResponse
import com.wutsi.platform.contact.service.ContactService
import com.wutsi.platform.core.logging.KVLogger
import org.springframework.stereotype.Service

@Service
public class SearchContactDelegate(
    private val service: ContactService,
    private val logger: KVLogger,
) {
    public fun invoke(request: SearchContactRequest): SearchContactResponse {
        logger.add("limit", request.limit)
        logger.add("offset", request.offset)

        val contacts = service.search(request)

        logger.add("count", contacts.size)
        return SearchContactResponse(
            contacts = contacts.map { it.toContact() }
        )
    }
}

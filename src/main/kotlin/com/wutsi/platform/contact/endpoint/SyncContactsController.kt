package com.wutsi.platform.contact.endpoint

import com.wutsi.platform.contact.`delegate`.SyncContactsDelegate
import com.wutsi.platform.contact.dto.SyncContactRequest
import com.wutsi.platform.contact.dto.SyncContactResponse
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.`annotation`.PostMapping
import org.springframework.web.bind.`annotation`.RequestBody
import org.springframework.web.bind.`annotation`.RestController
import javax.validation.Valid

@RestController
public class SyncContactsController(
    private val `delegate`: SyncContactsDelegate
) {
    @PostMapping("/v1/contacts/sync")
    @PreAuthorize(value = "hasAuthority('contact-manage')")
    public fun invoke(@Valid @RequestBody request: SyncContactRequest): SyncContactResponse =
        delegate.invoke(request)
}

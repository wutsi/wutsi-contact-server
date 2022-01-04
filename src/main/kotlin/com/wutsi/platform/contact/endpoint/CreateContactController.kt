package com.wutsi.platform.contact.endpoint

import com.wutsi.platform.contact.`delegate`.CreateContactDelegate
import com.wutsi.platform.contact.dto.CreateContactRequest
import com.wutsi.platform.contact.dto.CreateContactResponse
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.`annotation`.PostMapping
import org.springframework.web.bind.`annotation`.RequestBody
import org.springframework.web.bind.`annotation`.RestController
import javax.validation.Valid

@RestController
public class CreateContactController(
    private val `delegate`: CreateContactDelegate
) {
    @PostMapping("/v1/contacts")
    @PreAuthorize(value = "hasAuthority('contact-read')")
    public fun invoke(@Valid @RequestBody request: CreateContactRequest): CreateContactResponse =
        delegate.invoke(request)
}

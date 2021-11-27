package com.wutsi.platform.contact.endpoint

import com.wutsi.platform.contact.`delegate`.SearchContactDelegate
import com.wutsi.platform.contact.dto.SearchContactRequest
import com.wutsi.platform.contact.dto.SearchContactResponse
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.`annotation`.PostMapping
import org.springframework.web.bind.`annotation`.RequestBody
import org.springframework.web.bind.`annotation`.RestController
import javax.validation.Valid

@RestController
public class SearchContactController(
    private val `delegate`: SearchContactDelegate
) {
    @PostMapping("/v1/contacts/search")
    @PreAuthorize(value = "hasAuthority('contact-read')")
    public fun invoke(@Valid @RequestBody request: SearchContactRequest): SearchContactResponse =
        delegate.invoke(request)
}

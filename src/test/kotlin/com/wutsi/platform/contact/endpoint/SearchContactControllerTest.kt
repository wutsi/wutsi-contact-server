package com.wutsi.platform.contact.endpoint

import com.wutsi.platform.contact.dto.SearchContactRequest
import com.wutsi.platform.contact.dto.SearchContactResponse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/SearchContactController.sql"])
public class SearchContactControllerTest : AbstractSecuredController() {
    @LocalServerPort
    public val port: Int = 0

    private lateinit var url: String

    @BeforeEach
    override fun setUp() {
        super.setUp()

        url = "http://localhost:$port/v1/contacts/search"
    }

    @Test
    public fun invoke() {
        // WHEN
        val request = SearchContactRequest(
            accountId = 100L,
            tenantId = 1L
        )
        val response = rest.postForEntity(url, request, SearchContactResponse::class.java)

        // THEN
        assertEquals(200, response.statusCodeValue)

        val contacts = response.body.contacts.sortedBy { it.contactId }
        assertEquals(3, contacts.size)
        assertEquals(1L, contacts[0].contactId)
        assertEquals(2L, contacts[1].contactId)
        assertEquals(3L, contacts[2].contactId)
    }

    @Test
    public fun notFound() {
        // WHEN
        val request = SearchContactRequest(
            accountId = 999L,
            tenantId = 1L
        )
        val response = rest.postForEntity(url, request, SearchContactResponse::class.java)

        // THEN
        assertEquals(200, response.statusCodeValue)

        val contacts = response.body.contacts
        assertEquals(0, contacts.size)
    }
}

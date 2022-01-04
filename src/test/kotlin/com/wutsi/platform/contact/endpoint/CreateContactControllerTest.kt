package com.wutsi.platform.contact.endpoint

import com.wutsi.platform.contact.dao.ContactRepository
import com.wutsi.platform.contact.dto.CreateContactRequest
import com.wutsi.platform.contact.dto.CreateContactResponse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.jdbc.Sql
import org.springframework.web.client.HttpServerErrorException
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/CreateContactController.sql"])
public class CreateContactControllerTest : AbstractSecuredController() {
    @LocalServerPort
    public val port: Int = 0

    private lateinit var url: String

    @Autowired
    private lateinit var dao: ContactRepository

    @BeforeEach
    override fun setUp() {
        super.setUp()

        url = "http://localhost:$port/v1/contacts"
    }

    @Test
    public fun `add new contact`() {
        // WHEN
        val request = CreateContactRequest(
            contactId = 10
        )
        val response = rest.postForEntity(url, request, CreateContactResponse::class.java)

        // THEN
        assertEquals(200, response.statusCodeValue)

        val contact = dao.findById(response.body.id).get()
        assertEquals(request.contactId, contact.contactId)
    }

    @Test
    public fun `add existing contact`() {
        // WHEN
        val request = CreateContactRequest(
            contactId = 100
        )
        val response = rest.postForEntity(url, request, CreateContactResponse::class.java)

        // THEN
        assertEquals(200, response.statusCodeValue)

        val contact = dao.findById(response.body.id).get()
        assertEquals(request.contactId, contact.contactId)
    }

    @Test
    public fun `add self contact`() {
        // WHEN
        val request = CreateContactRequest(
            contactId = 1
        )
        assertThrows<HttpServerErrorException.InternalServerError> {
            rest.postForEntity(url, request, CreateContactResponse::class.java)
        }
    }
}

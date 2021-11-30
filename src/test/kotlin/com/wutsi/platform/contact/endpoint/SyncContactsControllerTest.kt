package com.wutsi.platform.contact.endpoint

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.platform.contact.dto.SyncContactRequest
import com.wutsi.platform.contact.dto.SyncContactResponse
import com.wutsi.platform.contact.event.EventURN
import com.wutsi.platform.contact.event.SyncContactPayload
import com.wutsi.platform.core.stream.EventStream
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.web.server.LocalServerPort
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SyncContactsControllerTest : AbstractSecuredController() {
    @LocalServerPort
    public val port: Int = 0

    @MockBean
    private lateinit var stream: EventStream

    private lateinit var url: String

    @BeforeEach
    override fun setUp() {
        super.setUp()

        url = "http://localhost:$port/v1/contacts/sync"
    }

    @Test
    public fun invoke() {
        val request = SyncContactRequest(
            phoneNumbers = listOf("a", "b", "c")
        )
        val response = rest.postForEntity(url, request, SyncContactResponse::class.java)

        assertEquals(200, response.statusCodeValue)

        val payload = argumentCaptor<SyncContactPayload>()
        verify(stream).enqueue(eq(EventURN.SYNC_REQUESTED.urn), payload.capture())

        assertEquals(1L, payload.firstValue.tenantId)
        assertEquals(1L, payload.firstValue.accountId)
        assertEquals(listOf("a", "b", "c"), payload.firstValue.phoneNumbers)
    }
}

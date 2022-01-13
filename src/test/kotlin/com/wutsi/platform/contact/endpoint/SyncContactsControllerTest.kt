package com.wutsi.platform.contact.endpoint

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.platform.contact.dto.SyncContactRequest
import com.wutsi.platform.contact.dto.SyncContactResponse
import com.wutsi.platform.contact.event.EventURN
import com.wutsi.platform.contact.event.SyncRequestPayload
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

    private lateinit var url: String

    @MockBean
    private lateinit var eventStream: EventStream

    @BeforeEach
    override fun setUp() {
        super.setUp()

        url = "http://localhost:$port/v1/contacts/sync"
    }

    @Test
    public fun invoke() {
        // WHEN
        val request = SyncContactRequest(
            phoneNumbers = listOf("237699505678", "237699505679")
        )
        val response = rest.postForEntity(url, request, SyncContactResponse::class.java)

        assertEquals(200, response.statusCodeValue)

        // THEN
        val payload = argumentCaptor<SyncRequestPayload>()
        verify(eventStream, times(2)).enqueue(eq(EventURN.SYNC_REQUEST.urn), payload.capture())

        assertEquals(USER_ID, payload.firstValue.accountId)
        assertEquals("237699505678", payload.firstValue.phoneNumber)

        assertEquals(USER_ID, payload.secondValue.accountId)
        assertEquals("237699505679", payload.secondValue.phoneNumber)
    }
}

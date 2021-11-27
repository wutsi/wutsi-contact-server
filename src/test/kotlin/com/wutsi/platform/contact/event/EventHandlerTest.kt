package com.wutsi.platform.contact.event

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.platform.contact.dao.ContactRepository
import com.wutsi.platform.core.stream.Event
import com.wutsi.platform.payment.event.EventURN
import com.wutsi.platform.payment.event.TransactionEventPayload
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.jdbc.Sql
import java.util.UUID

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/EventHandler.sql"])
internal class EventHandlerTest {
    @Autowired
    private lateinit var eventHandler: EventHandler

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var dao: ContactRepository

    @Test
    fun onTransferCreateContact() {
        // GIVEN
        val payload = createTransactionEventPayload(7, 777, "TRANSFER")

        // WHEN
        val event = Event(
            type = EventURN.TRANSACTION_SUCCESSFULL.urn,
            payload = objectMapper.writeValueAsString(payload)
        )
        eventHandler.onEvent(event)

        // THEN
        val contact =
            dao.findByAccountIdAndContactIdAndTenantId(payload.userId, payload.recipientId!!, payload.tenantId)
        assertTrue(contact.isPresent)
    }

    @Test
    fun onTransferWithExistingContact() {
        // GIVEN
        val payload = createTransactionEventPayload(100, 1, "TRANSFER")

        // WHEN
        val event = Event(
            type = EventURN.TRANSACTION_SUCCESSFULL.urn,
            payload = objectMapper.writeValueAsString(payload)
        )
        eventHandler.onEvent(event)

        // THEN
        val contact =
            dao.findByAccountIdAndContactIdAndTenantId(payload.userId, payload.recipientId!!, payload.tenantId)
        assertTrue(contact.isPresent)
    }

    private fun createTransactionEventPayload(userId: Long, recipientId: Long, type: String) = TransactionEventPayload(
        tenantId = 1,
        type = type,
        currency = "XAF",
        amount = 5000.0,
        recipientId = recipientId,
        userId = userId,
        transactionId = UUID.randomUUID().toString()
    )
}

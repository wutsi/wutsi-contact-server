package com.wutsi.platform.contact.event

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.platform.account.WutsiAccountApi
import com.wutsi.platform.account.dto.AccountSummary
import com.wutsi.platform.account.dto.SearchAccountResponse
import com.wutsi.platform.account.event.AccountCreatedPayload
import com.wutsi.platform.contact.dao.ContactRepository
import com.wutsi.platform.contact.endpoint.AbstractSecuredController
import com.wutsi.platform.core.stream.Event
import com.wutsi.platform.core.stream.EventTracingData
import com.wutsi.platform.payment.event.EventURN
import com.wutsi.platform.payment.event.TransactionEventPayload
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.test.context.jdbc.Sql
import java.util.UUID

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/EventHandler.sql"])
internal class EventHandlerTest : AbstractSecuredController() {
    @Autowired
    private lateinit var eventHandler: EventHandler

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var contactDao: ContactRepository

    @MockBean
    private lateinit var accountApi: WutsiAccountApi

    // Transfer
    @Test
    fun onTransferCreateContact() {
        // GIVEN
        val payload = createTransactionEventPayload(7, 777, "TRANSFER")

        // WHEN
        val event = Event(
            type = EventURN.TRANSACTION_SUCCESSFULL.urn,
            payload = objectMapper.writeValueAsString(payload),
            tracingData = EventTracingData(
                clientId = "foo",
                tenantId = TENANT_ID.toString(),
                traceId = UUID.randomUUID().toString()
            )
        )
        eventHandler.onEvent(event)

        // THEN
        val contact =
            contactDao.findByAccountIdAndContactIdAndTenantId(
                payload.accountId,
                payload.recipientId!!,
                TENANT_ID
            )
        assertTrue(contact.isPresent)
    }

    @Test
    fun onTransferWithExistingContact() {
        // GIVEN
        val payload = createTransactionEventPayload(100, 1, "TRANSFER")

        // WHEN
        val event = Event(
            type = EventURN.TRANSACTION_SUCCESSFULL.urn,
            payload = objectMapper.writeValueAsString(payload),
            tracingData = EventTracingData(
                clientId = "foo",
                tenantId = TENANT_ID.toString(),
                traceId = UUID.randomUUID().toString()
            )
        )
        eventHandler.onEvent(event)

        // THEN
        val contact =
            contactDao.findByAccountIdAndContactIdAndTenantId(
                payload.accountId,
                payload.recipientId!!,
                TENANT_ID
            )
        assertTrue(contact.isPresent)
    }

    private fun createTransactionEventPayload(accountId: Long, recipientId: Long, type: String) =
        TransactionEventPayload(
            type = type,
            currency = "XAF",
            amount = 5000.0,
            recipientId = recipientId,
            accountId = accountId,
            transactionId = UUID.randomUUID().toString()
        )

    // AccountCreated
    @Test
    @Sql(value = ["/db/clean.sql", "/db/EventHandler.sql"])
    fun onAccountCreated() {
        // GIVEN
        val payload = createAccountCreatedPayload(555L, "+237699505678")

        // WHEN
        val event = Event(
            type = com.wutsi.platform.account.event.EventURN.ACCOUNT_CREATED.urn,
            payload = objectMapper.writeValueAsString(payload),
            tracingData = EventTracingData(
                clientId = "foo",
                tenantId = TENANT_ID.toString(),
                traceId = UUID.randomUUID().toString()
            )
        )
        eventHandler.onEvent(event)

        // THEN
        val contact =
            contactDao.findByAccountIdAndContactIdAndTenantId(100, payload.accountId, TENANT_ID)
        assertTrue(contact.isPresent)
    }

    private fun createAccountCreatedPayload(accountId: Long, phoneNumber: String) = AccountCreatedPayload(
        accountId = accountId,
        phoneNumber = phoneNumber
    )

    // Sync
    @Test
    @Sql(value = ["/db/clean.sql", "/db/EventHandler.sql"])
    fun onSyncWithExistingPhone() {
        // GIVEN
        val account = AccountSummary(id = 555)
        doReturn(SearchAccountResponse(listOf(account))).whenever(accountApi).searchAccount(any())

        val payload = createSyncContactPayload(100, "+237699505678")

        // WHEN
        val event = createEvent(payload)
        eventHandler.onEvent(event)

        // THEN
        val contact = contactDao.findByAccountIdAndContactIdAndTenantId(100, 555, TENANT_ID)
        assertTrue(contact.isPresent)
    }

    @Test
    @Sql(value = ["/db/clean.sql", "/db/EventHandler.sql"])
    fun onSyncWithNewPhone() {
        // GIVEN
        val account = AccountSummary(id = 555)
        doReturn(SearchAccountResponse(listOf(account))).whenever(accountApi).searchAccount(any())

        val payload = createSyncContactPayload(100, "+237690000001")

        // WHEN
        val event = createEvent(payload)
        eventHandler.onEvent(event)

        // THEN
        val contact = contactDao.findByAccountIdAndContactIdAndTenantId(100, 555, TENANT_ID)
        assertTrue(contact.isPresent)
    }

    @Test
    @Sql(value = ["/db/clean.sql", "/db/EventHandler.sql"])
    fun onSyncSelfContact() {
        // GIVEN
        val account = AccountSummary(id = 100)
        doReturn(SearchAccountResponse(listOf(account))).whenever(accountApi).searchAccount(any())

        val payload = createSyncContactPayload(100, "+237690000001")

        // WHEN
        val event = createEvent(payload)
        assertThrows<DataIntegrityViolationException> {
            eventHandler.onEvent(event)
        }
    }

    private fun createEvent(payload: SyncRequestPayload) = Event(
        type = com.wutsi.platform.contact.event.EventURN.SYNC_REQUEST.urn,
        payload = objectMapper.writeValueAsString(payload),
        tracingData = EventTracingData(
            clientId = "foo",
            tenantId = TENANT_ID.toString(),
            traceId = UUID.randomUUID().toString()
        )
    )

    private fun createSyncContactPayload(accountId: Long, phoneNumber: String) = SyncRequestPayload(
        accountId = accountId,
        phoneNumber = phoneNumber
    )
}

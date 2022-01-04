package com.wutsi.platform.contact.event

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.platform.account.event.AccountCreatedPayload
import com.wutsi.platform.contact.service.ContactService
import com.wutsi.platform.contact.service.PhoneService
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.stream.Event
import com.wutsi.platform.payment.event.EventURN
import com.wutsi.platform.payment.event.TransactionEventPayload
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service

@Service
class EventHandler(
    private val objectMapper: ObjectMapper,
    private val contactService: ContactService,
    private val phoneService: PhoneService,
    private val logger: KVLogger,
) {
    @EventListener
    fun onEvent(event: Event) {
        if (EventURN.TRANSACTION_SUCCESSFULL.urn == event.type) {
            val payload = objectMapper.readValue(event.payload, TransactionEventPayload::class.java)
            onTransactionSuccessful(payload)
        } else if (com.wutsi.platform.account.event.EventURN.ACCOUNT_CREATED.urn == event.type) {
            val payload = objectMapper.readValue(event.payload, AccountCreatedPayload::class.java)
            onAccountCreated(payload)
        }
    }

    private fun onTransactionSuccessful(payload: TransactionEventPayload) {
        if (payload.type != "TRANSFER")
            return

        logger.add("tenant_id", payload.tenantId)
        logger.add("amount", payload.amount)
        logger.add("currency", payload.currency)
        logger.add("transaction_id", payload.transactionId)
        logger.add("accountId", payload.accountId)
        logger.add("recipient_id", payload.recipientId)

        val contact = contactService.addContact(payload)
        logger.add("contact_id", contact?.id)
    }

    private fun onAccountCreated(payload: AccountCreatedPayload) {
        logger.add("tenant_id", payload.tenantId)
        logger.add("account_id", payload.accountId)
        logger.add("phone_number", payload.phoneNumber)

        val count = phoneService.addContacts(payload)
        logger.add("count", count)
    }
}

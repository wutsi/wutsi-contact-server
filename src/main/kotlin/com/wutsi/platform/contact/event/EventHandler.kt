package com.wutsi.platform.contact.event

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.platform.contact.service.ContactService
import com.wutsi.platform.contact.service.PhoneService
import com.wutsi.platform.core.logging.RequestKVLogger
import com.wutsi.platform.core.stream.Event
import com.wutsi.platform.payment.event.EventURN
import com.wutsi.platform.payment.event.TransactionEventPayload
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service

@Service
class EventHandler(
    private val objectMapper: ObjectMapper,
    private val contactService: ContactService,
    private val phoneService: PhoneService,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(EventHandler::class.java)
    }

    @EventListener
    fun onEvent(event: Event) {
        LOGGER.info("onEvent(${event.type},...)")
        if (EventURN.TRANSACTION_SUCCESSFULL.urn == event.type) {
            val payload = objectMapper.readValue(event.payload, TransactionEventPayload::class.java)
            if (payload.type == "TRANSFER") {
                /* When a transfer is complete, add the recipient into the contact list of the sender */
                addContact(payload)
            }
        } else if (com.wutsi.platform.contact.event.EventURN.SYNC_REQUESTED.urn == event.type) {
            val payload = objectMapper.readValue(event.payload, SyncContactPayload::class.java)
            sync(payload)
        }
    }

    private fun addContact(payload: TransactionEventPayload) {
        val logger = RequestKVLogger()
        logger.add("tenant_id", payload.tenantId)
        logger.add("amount", payload.amount)
        logger.add("currency", payload.currency)
        logger.add("transaction_id", payload.transactionId)
        logger.add("user_id", payload.userId)
        logger.add("recipient_id", payload.recipientId)

        try {
            val contact = contactService.addContact(payload)

            logger.add("contact_added", contact != null)
        } catch (ex: Exception) {
            logger.setException(ex)
            throw ex
        } finally {
            logger.log()
        }
    }

    private fun sync(payload: SyncContactPayload) {
        val logger = RequestKVLogger()
        payload.phoneNumbers.forEach {
            logger.add("phone_number", it)
            logger.add("account_id", payload.accountId)
            logger.add("tenant_id", payload.tenantId)
            try {
                val phone = phoneService.addPhone(payload.accountId, payload.tenantId, it)
                logger.add("phone_added", phone != null)
            } catch (ex: Exception) {
                logger.setException(ex)
                throw ex
            } finally {
                logger.log()
            }
        }
    }
}

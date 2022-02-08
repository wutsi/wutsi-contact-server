package com.wutsi.platform.contact.service

import com.wutsi.platform.contact.dao.PhoneRepository
import com.wutsi.platform.contact.entity.PhoneEntity
import com.wutsi.platform.contact.event.SyncRequestPayload
import com.wutsi.platform.core.logging.KVLogger
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class PhoneService(
    private val dao: PhoneRepository,
    private val phoneNumberSanitizer: PhoneNumberSanitizer,
    private val tenantProvider: TenantProvider,
    private val logger: KVLogger,
) {
    @Transactional
    fun addPhone(payload: SyncRequestPayload): PhoneEntity? {
        val tenant = tenantProvider.get()
        val xphoneNumber = phoneNumberSanitizer.sanitize(payload.phoneNumber, tenant)
            ?: return null
        logger.add("phone_number_formatter", xphoneNumber)

        val opt = dao.findByAccountIdAndNumberAndTenantId(payload.accountId, xphoneNumber, tenant.id)
        if (opt.isPresent)
            return opt.get()

        return dao.save(
            PhoneEntity(
                accountId = payload.accountId,
                number = xphoneNumber,
                tenantId = tenant.id
            )
        )
    }

    fun findPhones(phoneNumber: String): List<PhoneEntity> {
        val tenant = tenantProvider.get()
        val xphoneNumber = phoneNumberSanitizer.sanitize(phoneNumber, tenant)
            ?: return emptyList()

        return dao.findByNumberAndTenantId(xphoneNumber, tenant.id)
    }
}

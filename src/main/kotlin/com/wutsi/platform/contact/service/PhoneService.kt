package com.wutsi.platform.contact.service

import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.wutsi.platform.account.event.AccountCreatedPayload
import com.wutsi.platform.contact.dao.PhoneRepository
import com.wutsi.platform.contact.entity.PhoneEntity
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class PhoneService(
    private val dao: PhoneRepository,
    private val contactService: ContactService,
    private val phoneUtil: PhoneNumberUtil,
) {
    @Transactional
    fun addPhone(accountId: Long, tenantId: Long, phoneNumber: String): PhoneEntity? {
        val xphoneNumber = normalizePhone(phoneNumber)
        val opt = dao.findByAccountIdAndNumberAndTenantId(accountId, xphoneNumber, tenantId)
        if (opt.isPresent)
            return null

        return dao.save(
            PhoneEntity(
                accountId = accountId,
                number = xphoneNumber,
                tenantId = tenantId
            )
        )
    }

    @Transactional
    fun addContacts(payload: AccountCreatedPayload): Int {
        val phones = dao.findByNumberAndTenantId(normalizePhone(payload.phoneNumber), payload.tenantId)
        var added = 0
        phones.forEach {
            if (contactService.addContact(it.accountId, payload.accountId, payload.tenantId) != null)
                added++
        }
        return added
    }

    private fun normalizePhone(phoneNumber: String): String {
        return try {
            val number = if (phoneNumber.startsWith("+")) phoneNumber else "+$phoneNumber"
            val phone = phoneUtil.parse(number, "")
            phoneUtil.format(phone, PhoneNumberUtil.PhoneNumberFormat.E164)
        } catch (ex: Exception) {
            phoneNumber
        }
    }
}

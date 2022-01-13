package com.wutsi.platform.contact.service

import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.wutsi.platform.tenant.dto.Tenant
import org.springframework.stereotype.Service

@Service
class PhoneNumberSanitizer(
    private val phoneUtil: PhoneNumberUtil,
) {
    fun sanitize(phoneNumber: String, tenant: Tenant): String? {
        // Trim
        var xphoneNumber = phoneNumber.trimStart('0') // Remove leading 0
            .replace("\\s".toRegex(), "") // Remove all empty strings

        // Verification
        if (xphoneNumber.startsWith("#") || xphoneNumber.startsWith("*"))
            return null

        // International prefix
        if (!xphoneNumber.startsWith("+"))
            xphoneNumber = "+$xphoneNumber"

        // Is supported by the tenant?
        val prefixes = tenant.mobileCarriers.flatMap { it.phonePrefixes }.flatMap { it.prefixes }
        prefixes.find { xphoneNumber.startsWith(it) }
            ?: return null

        // Format
        val number = phoneUtil.parse(xphoneNumber, "")
        return phoneUtil.format(number, PhoneNumberUtil.PhoneNumberFormat.E164)
    }
}

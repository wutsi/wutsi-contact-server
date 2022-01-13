package com.wutsi.platform.contact.service

import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.wutsi.platform.tenant.dto.Logo
import com.wutsi.platform.tenant.dto.MobileCarrier
import com.wutsi.platform.tenant.dto.PhonePrefix
import com.wutsi.platform.tenant.dto.Tenant
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

internal class PhoneNumberSanitizerTest {
    private val tenant = Tenant(
        id = 1,
        name = "test",
        logos = listOf(
            Logo(type = "PICTORIAL", url = "http://www.goole.com/images/1.png")
        ),
        countries = listOf("CM"),
        languages = listOf("en", "fr"),
        currency = "XAF",
        domainName = "www.wutsi.com",
        mobileCarriers = listOf(
            MobileCarrier(
                code = "mtn",
                name = "MTN",
                countries = listOf("CM", "CD"),
                phonePrefixes = listOf(
                    PhonePrefix(
                        country = "CM",
                        prefixes = listOf("+23795", "+23767")
                    ),
                ),
                logos = listOf(
                    Logo(type = "PICTORIAL", url = "http://www.goole.com/images/mtn.png")
                )
            ),
            MobileCarrier(
                code = "orange",
                name = "ORANGE",
                countries = listOf("CM"),
                phonePrefixes = listOf(
                    PhonePrefix(
                        country = "CM",
                        prefixes = listOf("+237745")
                    ),
                ),
                logos = listOf(
                    Logo(type = "PICTORIAL", url = "http://www.goole.com/images/orange.png")
                )
            )
        ),
    )
    private val sanitizer: PhoneNumberSanitizer = PhoneNumberSanitizer(PhoneNumberUtil.getInstance())

    @Test
    fun e164() {
        assertEquals("+237670000001", sanitizer.sanitize("+237670000001", tenant))
    }

    @Test
    fun noPlus() {
        assertEquals("+237670000001", sanitizer.sanitize("237670000001", tenant))
    }

    @Test
    fun widthSpaces() {
        assertEquals("+237670000001", sanitizer.sanitize("+237 6 70 00 00 01", tenant))
    }

    @Test
    fun leading00() {
        assertEquals("+237670000001", sanitizer.sanitize("00 237 6 70 00 00 01", tenant))
    }

    @Test
    fun startsWithStar() {
        assertNull(sanitizer.sanitize("*132#", tenant))
    }

    @Test
    fun startWithDash() {
        assertNull(sanitizer.sanitize("#132#", tenant))
    }

    @Test
    fun localPhoneNumber() {
        assertNull(sanitizer.sanitize("6 70 10 00 01", tenant))
    }

    @Test
    fun prefixNotSupported() {
        assertNull(sanitizer.sanitize("+447904358808", tenant))
    }
}

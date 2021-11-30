package com.wutsi.platform.contact.endpoint

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.platform.account.WutsiAccountApi
import com.wutsi.platform.account.dto.AccountSummary
import com.wutsi.platform.account.dto.SearchAccountRequest
import com.wutsi.platform.account.dto.SearchAccountResponse
import com.wutsi.platform.contact.dao.ContactRepository
import com.wutsi.platform.contact.dao.PhoneRepository
import com.wutsi.platform.contact.dto.SyncContactRequest
import com.wutsi.platform.contact.dto.SyncContactResponse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/SyncContactsController.sql"])
public class SyncContactsControllerTest : AbstractSecuredController() {
    @LocalServerPort
    public val port: Int = 0

    @MockBean
    private lateinit var accountApi: WutsiAccountApi

    private lateinit var url: String

    @Autowired
    private lateinit var phoneDao: PhoneRepository

    @Autowired
    private lateinit var contactDao: ContactRepository

    @BeforeEach
    override fun setUp() {
        super.setUp()

        url = "http://localhost:$port/v1/contacts/sync"
    }

    @Test
    public fun invoke() {
        // GIVEN
        val req1 = SearchAccountRequest(phoneNumber = "+237699505678")
        val acc1 = AccountSummary(555)
        doReturn(SearchAccountResponse(listOf(acc1))).whenever(accountApi).searchAccount(req1)

        val req2 = SearchAccountRequest(phoneNumber = "+237699505679")
        val acc2 = AccountSummary(666)
        doReturn(SearchAccountResponse(listOf(acc2))).whenever(accountApi).searchAccount(req2)

        // WHEN
        val request = SyncContactRequest(
            phoneNumbers = listOf("237699505678", "237699505679")
        )
        val response = rest.postForEntity(url, request, SyncContactResponse::class.java)

        assertEquals(200, response.statusCodeValue)

        // THEN
        val phones = phoneDao.findByAccountIdAndTenantId(1, 1).sortedBy { it.number }
        assertEquals(2, phones.size)
        assertEquals("+237699505678", phones[0].number)
        assertEquals("+237699505679", phones[1].number)

        val contacts = contactDao.findAll().sortedBy { it.contactId }
        assertEquals(2, contacts.size)
        assertEquals(1L, contacts[0].accountId)
        assertEquals(555L, contacts[0].contactId)

        assertEquals(1L, contacts[1].accountId)
        assertEquals(666L, contacts[1].contactId)
    }
}

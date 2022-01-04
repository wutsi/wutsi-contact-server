package com.wutsi.platform.contact.dao

import com.wutsi.platform.contact.entity.ContactEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface ContactRepository : CrudRepository<ContactEntity, Long> {
    fun findByAccountIdAndTenantId(accountId: Long, tenantId: Long?, pageable: Pageable): List<ContactEntity>

    fun findByAccountIdAndContactIdAndTenantId(
        accountId: Long,
        contactId: Long,
        tenantId: Long
    ): Optional<ContactEntity>

    fun findByAccountIdAndContactIdInAndTenantId(
        accountId: Long,
        contactId: List<Long>,
        tenantId: Long,
        pageable: Pageable
    ): List<ContactEntity>
}

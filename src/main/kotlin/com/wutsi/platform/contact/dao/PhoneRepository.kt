package com.wutsi.platform.contact.dao

import com.wutsi.platform.contact.entity.PhoneEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface PhoneRepository : CrudRepository<PhoneEntity, Long> {
    fun findByAccountIdAndTenantId(accountId: Long, tenantId: Long): List<PhoneEntity>

    fun findByAccountIdAndNumberAndTenantId(
        accountId: Long,
        number: String,
        tenantId: Long
    ): Optional<PhoneEntity>
}

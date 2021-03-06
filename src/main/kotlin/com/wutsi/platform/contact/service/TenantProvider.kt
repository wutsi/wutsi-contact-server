package com.wutsi.platform.contact.service

import com.wutsi.platform.core.tracing.TracingContext
import com.wutsi.platform.tenant.WutsiTenantApi
import com.wutsi.platform.tenant.dto.Tenant
import org.springframework.stereotype.Service

@Service
public class TenantProvider(
    private val tenantApi: WutsiTenantApi,
    private val tracingContext: TracingContext
) {
    fun get(): Tenant =
        get(id())

    fun get(id: Long): Tenant =
        tenantApi.getTenant(id).tenant

    fun id(): Long =
        tracingContext.tenantId()!!.toLong()
}

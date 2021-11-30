package com.wutsi.platform.contact.endpoint

import com.wutsi.platform.core.security.ApiKeyProvider
import com.wutsi.platform.core.security.SubjectType
import com.wutsi.platform.core.security.SubjectType.USER
import com.wutsi.platform.core.security.spring.SpringApiKeyRequestInterceptor
import com.wutsi.platform.core.security.spring.SpringAuthorizationRequestInterceptor
import com.wutsi.platform.core.security.spring.jwt.JWTBuilder
import com.wutsi.platform.core.test.TestApiKeyProvider
import com.wutsi.platform.core.test.TestRSAKeyProvider
import com.wutsi.platform.core.test.TestTokenProvider
import com.wutsi.platform.core.test.TestTracingContext
import com.wutsi.platform.core.tracing.TracingContext
import com.wutsi.platform.core.tracing.spring.SpringTracingRequestInterceptor
import com.wutsi.platform.core.util.URN
import org.junit.jupiter.api.BeforeEach
import org.springframework.web.client.RestTemplate

abstract class AbstractSecuredController {
    private lateinit var tracingContext: TracingContext
    private lateinit var apiKeyProvider: ApiKeyProvider

    protected lateinit var rest: RestTemplate

    @BeforeEach
    open fun setUp() {
        tracingContext = TestTracingContext(tenantId = "1")
        apiKeyProvider = TestApiKeyProvider("00000000-00000000-00000000-00000000")

        rest = createResTemplate()
    }

    fun createResTemplate(
        scope: List<String> = listOf("contact-read", "contact-manage"),
        subjectId: Long = 1,
        subjectType: SubjectType = USER,
        admin: Boolean = false
    ): RestTemplate {
        val rest = RestTemplate()

        val tokenProvider = TestTokenProvider(
            JWTBuilder(
                subject = subjectId.toString(),
                name = URN.of("user", subjectId.toString()).value,
                subjectType = subjectType,
                scope = scope,
                keyProvider = TestRSAKeyProvider(),
                admin = admin
            ).build()
        )

        rest.interceptors.add(SpringTracingRequestInterceptor(tracingContext))
        rest.interceptors.add(SpringAuthorizationRequestInterceptor(tokenProvider))
        rest.interceptors.add(SpringApiKeyRequestInterceptor(apiKeyProvider))
        return rest
    }
}

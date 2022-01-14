package com.wutsi.platform.contact.config

import com.wutsi.platform.contact.service.ApplicationTokenInitializer
import org.springframework.context.annotation.Configuration
import javax.annotation.PostConstruct

@Configuration
class ApplicationTokenConfiguration(
    private val initializer: ApplicationTokenInitializer
) {
    @PostConstruct
    fun init() {
        initializer.init()
    }
}

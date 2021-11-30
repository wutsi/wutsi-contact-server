package com.wutsi.platform.contact.config

import com.wutsi.platform.core.stream.EventStream
import com.wutsi.platform.core.stream.EventSubscription
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
public class AccountApiConfiguration(
    private val eventStream: EventStream
) {
    @Bean
    fun accountSubscription() = EventSubscription("wutsi-account", eventStream)
}

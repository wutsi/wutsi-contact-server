package com.wutsi.platform.contact.config

import com.wutsi.platform.core.stream.EventStream
import com.wutsi.platform.core.stream.EventSubscription
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
public class PaymentApiConfiguration(
    private val eventStream: EventStream
) {
    @Bean
    fun paymentSubscription() = EventSubscription("wutsi-payment", eventStream)
}

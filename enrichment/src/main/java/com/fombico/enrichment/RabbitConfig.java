package com.fombico.enrichment;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Qualifier("enrichment.queue")
    @Bean
    public Queue enrichmentRequestsQueue() {
        return new Queue("enrichment.exchange.requests");
    }

    @Qualifier("enrichment.direct")
    @Bean
    public DirectExchange enrichmentRequestsExchange() {
        return new DirectExchange("enrichment.direct");
    }

    @Qualifier("enrichment.binding")
    @Bean
    public Binding enrichmentRequestsBinding(
            @Qualifier("enrichment.direct") DirectExchange exchange,
            @Qualifier("enrichment.queue") Queue queue) {
        return BindingBuilder.bind(queue)
                .to(exchange)
                .with("storeNumber");
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}

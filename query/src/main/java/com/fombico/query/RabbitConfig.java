package com.fombico.query;

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

    @Qualifier("query.queue")
    @Bean
    public Queue queryRequestsQueue() {
        return new Queue("loadtest.query.exchange.requests");
    }

    @Qualifier("query.direct")
    @Bean
    public DirectExchange queryRequestsExchange() {
        return new DirectExchange("loadtest.query.direct");
    }

    @Qualifier("query.binding")
    @Bean
    public Binding queryRequestsBinding(
            @Qualifier("query.direct") DirectExchange exchange,
            @Qualifier("query.queue") Queue queue) {
        return BindingBuilder.bind(queue)
                .to(exchange)
                .with("storeNumber");
    }

    @Qualifier("enrichment.direct")
    @Bean
    public DirectExchange enrichmentExchange() {
        return new DirectExchange("loadtest.enrichment.direct");
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}

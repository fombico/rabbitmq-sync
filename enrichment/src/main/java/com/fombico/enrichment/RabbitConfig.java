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

    @Qualifier("enrichment.queue.ping")
    @Bean
    public Queue enrichmentRequestsPingQueue() {
        return new Queue("enrichment.exchange.requests.ping");
    }

    @Qualifier("enrichment.direct.ping")
    @Bean
    public DirectExchange enrichmentRequestsPingExchange() {
        return new DirectExchange("enrichment.direct.ping");
    }

    @Qualifier("enrichment.binding.ping")
    @Bean
    public Binding enrichmentRequestsPingBinding(
            @Qualifier("enrichment.direct.ping") DirectExchange exchange,
            @Qualifier("enrichment.queue.ping") Queue queue) {
        return BindingBuilder.bind(queue)
                .to(exchange)
                .with("storeNumber");
    }


    @Qualifier("enrichment.queue.pong")
    @Bean
    public Queue enrichmentRequestsPongQueue() {
        return new Queue("enrichment.exchange.requests.pong");
    }

    @Qualifier("enrichment.direct.pong")
    @Bean
    public DirectExchange enrichmentRequestsPongExchange() {
        return new DirectExchange("enrichment.direct.pong");
    }

    @Qualifier("enrichment.binding.pong")
    @Bean
    public Binding enrichmentRequestsPongBinding(
            @Qualifier("enrichment.direct.pong") DirectExchange exchange,
            @Qualifier("enrichment.queue.pong") Queue queue) {
        return BindingBuilder.bind(queue)
                .to(exchange)
                .with("storeNumber");
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}

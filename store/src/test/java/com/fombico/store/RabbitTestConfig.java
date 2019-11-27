package com.fombico.store;

import com.github.fridujo.rabbitmq.mock.compatibility.MockConnectionFactoryFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class RabbitTestConfig {
    @Bean
    ConnectionFactory connectionFactory() {
        return new CachingConnectionFactory(
                MockConnectionFactoryFactory
                        .build());
    }

    @Qualifier("query.queue")
    @Bean
    public Queue queryRequestsQueue() {
        return new Queue("query.exchange.requests");
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
}

package com.fombico.query;

import com.github.fridujo.rabbitmq.mock.compatibility.MockConnectionFactoryFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
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
}

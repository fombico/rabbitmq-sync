package com.fombico.enrichment;

import com.fombico.enrichment.models.QueryRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MessageListener {

    @RabbitListener(queues = "enrichment.exchange.requests.ping")
    public String handlePing(QueryRequest payload) throws InterruptedException {
        log.info("ping request received: " + payload);
        Thread.sleep(300);
        return "Ping";
    }

    @RabbitListener(queues = "enrichment.exchange.requests.pong")
    public String handlePong(QueryRequest payload) throws InterruptedException {
        log.info("pong request received: " + payload);
        Thread.sleep(3000);
        return "Pong";
    }
}

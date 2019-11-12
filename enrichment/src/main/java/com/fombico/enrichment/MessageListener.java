package com.fombico.enrichment;

import com.fombico.enrichment.models.QueryRequest;
import com.fombico.enrichment.models.QueryResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
@Slf4j
public class MessageListener {

    @RabbitListener(queues = "enrichment.exchange.requests")
    public QueryResponse handleQuery(QueryRequest payload) {
        log.info("query request received: " + payload);

        Random random = new Random();
        QueryResponse response = QueryResponse.builder()
                .firstName("Joe " + random.nextInt(100))
                .lastName("Doe " + random.nextInt(100))
                .build();

        log.info("response " + response);
        return response;
    }
}

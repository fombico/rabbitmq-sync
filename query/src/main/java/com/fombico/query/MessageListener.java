package com.fombico.query;

import com.fombico.query.models.QueryDto;
import com.fombico.query.models.QueryRequest;
import com.fombico.query.models.QueryResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class MessageListener {

    private static final String ROUTING_KEY = "storeNumber";
    private RabbitTemplate rabbitTemplate;
    private DirectExchange enrichmentExchange;

    public MessageListener(RabbitTemplate rabbitTemplate,
                           @Qualifier("enrichment.direct") DirectExchange enrichmentExchange) {
        this.rabbitTemplate = rabbitTemplate;
        this.enrichmentExchange = enrichmentExchange;
    }

    @RabbitListener(queues = "query.exchange.requests")
    public QueryResponse handleQuery(QueryRequest payload) {
        log.info("query request received: " + payload);

        QueryDto dto = QueryDto.builder()
                .storeNumber(payload.getStoreNumber())
                .userId(payload.getUserId())
                .queryId(UUID.randomUUID().toString())
                .build();

        QueryResponse response = rabbitTemplate.convertSendAndReceiveAsType(enrichmentExchange.getName(), ROUTING_KEY, dto, ParameterizedTypeReference.forType(QueryResponse.class));
        log.info("query response received: " + response);
        return response;
    }
}

package com.fombico.store;

import com.fombico.store.models.QueryRequest;
import com.fombico.store.models.QueryResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
@Slf4j
public class ApiController {

    private static final String ROUTING_KEY = "storeNumber";
    private final RabbitTemplate template;
    private final String storeNumber;
    private final DirectExchange queryExchange;

    public ApiController(RabbitTemplate template,
                         @Value("${storeNumber}") String storeNumber,
                         @Qualifier("query.direct") DirectExchange queryExchange) {
        this.template = template;
        this.storeNumber = storeNumber;
        this.queryExchange = queryExchange;
    }

    @PostMapping("/query")
    public QueryResponse query() {
        QueryRequest queryRequest = QueryRequest.builder()
                .userId(userId().toString())
                .storeNumber(storeNumber)
                .build();
        log.info("Sending query for store number " + storeNumber);
        QueryResponse response = template.convertSendAndReceiveAsType(queryExchange.getName(), ROUTING_KEY, queryRequest, ParameterizedTypeReference.forType(QueryResponse.class));
        log.info("Response: " + response);
        return response;
    }

    private Integer userId() {
        return new Random().nextInt(1000000) + 1000000;
    }
}

package com.fombico.query;

import com.fombico.query.models.QueryDto;
import com.fombico.query.models.QueryRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.UUID;

import static java.util.Collections.emptyList;

@Component
@Slf4j
public class MessageListener {

    private static final String ROUTING_KEY = "storeNumber";
    private RabbitTemplate rabbitTemplate;
    private DirectExchange pingExchange;
    private DirectExchange pongExchange;

    public MessageListener(RabbitTemplate rabbitTemplate,
                           @Qualifier("enrichment.direct.ping") DirectExchange pingExchange,
                           @Qualifier("enrichment.direct.pong") DirectExchange pongExchange) {
        this.rabbitTemplate = rabbitTemplate;
        this.rabbitTemplate.setReceiveTimeout(10000L);
        this.rabbitTemplate.setReplyTimeout(10000L);
        this.pingExchange = pingExchange;
        this.pongExchange = pongExchange;
    }

    @RabbitListener(queues = "query.exchange.requests")
    public String handleQuery(QueryRequest payload) {
        log.info("query request received: " + payload);

        QueryDto dto = QueryDto.builder()
                .storeNumber(payload.getStoreNumber())
                .userId(payload.getUserId())
                .queryId(UUID.randomUUID().toString())
                .build();

        Mono<String> mono1 = callEnrichment(pongExchange, dto)
                .subscribeOn(Schedulers.parallel());
        Mono<String> mono2 = emptyMono()
                .subscribeOn(Schedulers.parallel());
        Mono<String> mono3 = callEnrichment(pingExchange, dto)
                .subscribeOn(Schedulers.parallel());

        List<String> responses = Flux.merge(mono1, mono2, mono3)
                .collectList()
                .blockOptional()
                .orElse(emptyList());

        String result = String.join(" ", responses);
        log.info("Response {}", result);
        return result;
    }

    private Mono<String> callEnrichment(Exchange exchange, QueryDto dto) {
        return Mono.fromCallable(() -> {
            log.info("Making request to {}", exchange.getName());
            String response = rabbitTemplate.convertSendAndReceiveAsType(exchange.getName(), ROUTING_KEY, dto, ParameterizedTypeReference.forType(String.class));
            log.info("Received response {}", response);
            return response;
        });
    }

    private Mono<String> emptyMono() {
        log.info("Returning no result");
        return Mono.empty();
    }

}

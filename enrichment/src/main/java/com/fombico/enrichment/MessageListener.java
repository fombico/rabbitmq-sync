package com.fombico.enrichment;

import com.fombico.enrichment.models.QueryRequest;
import com.fombico.enrichment.models.QueryResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Slf4j
public class MessageListener {

    private static final int TIMEOUT = 65 * 1000;
    private static AtomicInteger counter = new AtomicInteger();


    private Random random = new Random();
    private RestTemplateBuilder restTemplateBuilder;

    public MessageListener(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplateBuilder = restTemplateBuilder;
    }

    @RabbitListener(queues = "loadtest.enrichment.exchange.requests")
    public QueryResponse handleQuery(QueryRequest payload) {
        int index = counter.incrementAndGet();
        log.info("{} query request received {}", index, payload);

        try {
            RestTemplate restTemplate = restTemplateBuilder
                    .setConnectTimeout(Duration.ofMillis(TIMEOUT))
                    .setReadTimeout(Duration.ofMillis(TIMEOUT))
                    .build();
            restTemplate.postForEntity("http://localhost:9000/sendMessage", "", Void.class);
        } catch (Exception ignored) {
        }

        QueryResponse response = QueryResponse.builder()
                .firstName("Joe " + random.nextInt(100))
                .lastName("Doe " + random.nextInt(100))
                .build();
        log.info("{} response {}", index, response);
        return response;
    }
}

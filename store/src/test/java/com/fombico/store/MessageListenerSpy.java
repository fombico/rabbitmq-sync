package com.fombico.store;

import com.fombico.store.models.QueryRequest;
import com.fombico.store.models.QueryResponse;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class MessageListenerSpy {

    private QueryResponse queryResponse;
    private QueryRequest lastPayload;

    public void setResponse(QueryResponse queryResponse) {
        this.queryResponse = queryResponse;
    }

    public QueryRequest getLastPayload() {
        return lastPayload;
    }

    @RabbitListener(queues = "query.exchange.requests")
    public QueryResponse handleQuery(QueryRequest payload) {
        lastPayload = payload;
        return queryResponse;
    }
}

package com.fombico.query;

import com.fombico.query.models.QueryDto;
import com.fombico.query.models.QueryRequest;
import com.fombico.query.models.QueryResponse;
import com.fombico.query.utils.EmbeddedInMemoryQpidBrokerRule;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.core.ParameterizedTypeReference.forType;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MessageListenerTest {

    @ClassRule
    public static EmbeddedInMemoryQpidBrokerRule qpidBrokerRule = new EmbeddedInMemoryQpidBrokerRule();

    @Value("${qpid.amqp_port}")
    int amqpPort;

    @Autowired
    MessageConverter messageConverter;

    @MockBean
    RabbitTemplate rabbitTemplate;

    @Captor
    ArgumentCaptor<QueryDto> queryDtoCaptor;

    @Test
    public void handleQuery_handlesQueryRequest() {
        QueryResponse expectedResponse = QueryResponse.builder()
                .firstName("fName")
                .lastName("lName")
                .build();

        when(rabbitTemplate.convertSendAndReceiveAsType(
                eq("enrichment.direct"),
                eq("storeNumber"),
                any(QueryDto.class),
                eq(forType(QueryResponse.class))))
                .thenReturn(expectedResponse);

        QueryRequest queryRequest = QueryRequest.builder()
                .storeNumber("1000")
                .userId("userId")
                .build();
        QueryResponse actualResponse = sendMessage(queryRequest);

        assertThat(actualResponse).isEqualTo(expectedResponse);

        verify(rabbitTemplate).convertSendAndReceiveAsType(
                eq("enrichment.direct"),
                eq("storeNumber"),
                queryDtoCaptor.capture(),
                eq(forType(QueryResponse.class)));

        QueryDto queryDto = queryDtoCaptor.getValue();
        assertThat(queryDto).isNotNull();
        assertThat(queryDto.getQueryId()).isNotEmpty();
        assertThat(queryDto.getStoreNumber()).isEqualTo(queryRequest.getStoreNumber());
        assertThat(queryDto.getUserId()).isEqualTo(queryRequest.getUserId());
    }

    private QueryResponse sendMessage(QueryRequest request) {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost("localhost");
        connectionFactory.setPort(amqpPort);

        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        QueryResponse response = template.convertSendAndReceiveAsType(
                "query.direct",
                "storeNumber",
                request,
                forType(QueryResponse.class));

        connectionFactory.destroy();

        return response;
    }
}
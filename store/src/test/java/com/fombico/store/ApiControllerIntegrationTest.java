package com.fombico.store;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fombico.store.models.QueryRequest;
import com.fombico.store.models.QueryResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApiControllerIntegrationTest {

    @Autowired
    MessageListenerSpy messageListenerSpy;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ApiController apiController;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(apiController)
                .build();
    }

    @Test
    public void postQuery_returnsQueryResponse() throws Exception {
        QueryResponse expectedQueryResponse = QueryResponse.builder()
                .firstName("John")
                .lastName("Doe")
                .build();
        messageListenerSpy.setResponse(expectedQueryResponse);

        String responseBody = mockMvc.perform(post("/query"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        QueryResponse actualQueryResponse = objectMapper.readValue(responseBody, QueryResponse.class);
        assertThat(actualQueryResponse).isEqualTo(expectedQueryResponse);

        QueryRequest queryRequest = messageListenerSpy.getLastPayload();
        assertThat(queryRequest).isNotNull();
        assertThat(queryRequest.getUserId()).isNotNull();
        assertThat(queryRequest.getStoreNumber()).isEqualTo("1000");
    }
}

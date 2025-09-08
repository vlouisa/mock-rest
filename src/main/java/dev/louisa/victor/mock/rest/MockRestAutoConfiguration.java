package dev.louisa.victor.mock.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.web.servlet.MockMvc;

@Configuration
public class MockRestAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
    
    @Bean
    @ConditionalOnMissingBean
    public MockRest mockRest(MockMvc mockMvc, ObjectMapper objectMapper) {
        return new MockRest(mockMvc, objectMapper);
    }
}
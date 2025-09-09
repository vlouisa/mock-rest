package dev.louisa.victor.mock.rest.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@FunctionalInterface
public interface RequestConfigurer {
     void apply(MockHttpServletRequestBuilder request);

    static RequestConfigurer body(ObjectMapper mapper, Object body) {
        return request -> {
            setRequestBody( body, request, mapper);
            request.contentType(MediaType.APPLICATION_JSON);
        };
    }

    private static void setRequestBody(Object body, MockHttpServletRequestBuilder request, ObjectMapper mapper) {
        try {
            request.content(mapper.writeValueAsString(body));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }

    static RequestConfigurer header(String name, String value) {
        return request -> request.header(name, value);
    }

    static RequestConfigurer jwt(String token) {
        return request -> request.header(HttpHeaders.AUTHORIZATION, "Bearer " + token);
    }
}
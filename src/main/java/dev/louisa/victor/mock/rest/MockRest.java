package dev.louisa.victor.mock.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RequiredArgsConstructor
@Slf4j
public class MockRest {
    private final MockMvc mockMvc;
    private final ObjectMapper mapper;

    public RequestBuilder post(String uri, Object... uriVars) {
        return new RequestBuilder(mockMvc, mapper, MockMvcRequestBuilders.post(resolveUri(uri, uriVars)));
    }

    public RequestBuilder get(String uri, Object... uriVars) {
        return new RequestBuilder(mockMvc, mapper, MockMvcRequestBuilders.get(resolveUri(uri, uriVars)));
    }

    public RequestBuilder put(String uri, Object... uriVars) {
        return new RequestBuilder(mockMvc, mapper, MockMvcRequestBuilders.put(resolveUri(uri, uriVars)));
    }

    public RequestBuilder patch(String uri, Object... uriVars) {
        return new RequestBuilder(mockMvc, mapper, MockMvcRequestBuilders.patch(resolveUri(uri, uriVars)));
    }

    public RequestBuilder delete(String uri, Object... uriVars) {
        return new RequestBuilder(mockMvc, mapper, MockMvcRequestBuilders.delete(resolveUri(uri, uriVars)));
    }

    private String resolveUri(String uri, Object... uriVars) {
        return org.springframework.web.util.UriComponentsBuilder
                .fromUriString(uri)
                .buildAndExpand(uriVars)
                .toUriString();
    }

    // --- Nested fluent RequestBuilder ---
    @RequiredArgsConstructor
    public static class RequestBuilder {
        private final MockMvc mockMvc;
        private final ObjectMapper mapper;
        private final MockHttpServletRequestBuilder request;

        private HttpStatus expectedStatus = null;
        private final Map<String, String> expectedResponseHeaders = new HashMap<>();

        private RequestPostProcessor userPostProcessor;

        public RequestBuilder body(Object body) throws Exception {
            request.content(mapper.writeValueAsString(body));
            request.contentType(MediaType.APPLICATION_JSON);
            return this;
        }

        public RequestBuilder withRequestHeader(String name, String value) {
            request.header(name, value);
            return this;
        }

        public RequestBuilder withJwt(String token) {
            this.userPostProcessor = addTokenToRequest(token);
            return this;
        }

        private RequestPostProcessor addTokenToRequest(String token) {
            return request -> {
                request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
                return request;
            };
        }

        public RequestBuilder expectResponseStatus(HttpStatus status) {
            this.expectedStatus = status;
            return this;
        }

        public RequestBuilder expectResponseHeader(String name, String value) {
            expectedResponseHeaders.put(name, value);
            return this;
        }

        private ResultActions exchange() throws Exception {
            ResultActions actions;
            if (userPostProcessor != null) {
                actions = mockMvc.perform(request.with(userPostProcessor));
            } else {
                actions = mockMvc.perform(request);
            }

            logDetails(actions);

            if (expectedStatus != null) {
                actions.andExpect(status().is(expectedStatus.value()));
            }

            expectedResponseHeaders.forEach((key, value) -> assertResponseHeader(actions, key, value));

            return actions;
        }

        private void assertResponseHeader(ResultActions actions, String key, String value) {
            assertHeader(actions, key, value, r -> r.getResponse().getHeader(key), "response");
        }

        private static void assertHeader(ResultActions actions, String key, String value, Function<MvcResult, String> actualHeaderExtractor, String payloadType) {
            try {
                actions.andExpect(r -> {
                    String header = actualHeaderExtractor.apply(r);
                    if (header == null) {
                        throw new AssertionError("Expected " + payloadType + " header '" + key + "' to exist, but was not found");
                    }
                    if (!header.equals(value)) {
                        throw new AssertionError("Expected " + payloadType + " header '" + key + "' to contain value '" + value + "', but was: " + header);
                    }
                });
            } catch (Exception e) {
                throw new AssertionError(e);
            }
        }
        public void expectNoResponseBody() throws Exception {
            ResultActions actions = exchange();
            MvcResult result = actions.andReturn();
            String content = result.getResponse().getContentAsString();
            if (!content.isBlank()) {
                throw new AssertionError("Expected no body, but response contained: " + content);
            }
        }

        public <T> T expectResponseBody(Class<T> type) throws Exception {
            ResultActions actions = exchange(); // ensures status check runs if configured
            MvcResult result = actions.andReturn();
            final String content = result.getResponse().getContentAsString();
            if (content.isBlank()) {
                throw new AssertionError("Expected body of type '" + type.getSimpleName() + "', but no body present" + content);
            }
            return map(content, type);
        }

        private <T> T map(String json, Class<T> type) {
            try {
                return mapper.readValue(json, type);
            } catch (JsonProcessingException e) {
                throw new AssertionError("Failed to parse response body to " + type.getSimpleName() + ": " + json, e);
            }
        }

        private void logDetails(ResultActions actions) {
            log.info("------------ REQUEST DETAILS -----------");
            MockRestLogger.log(actions.andReturn().getRequest());
            log.info("----------- RESPONSE DETAILS -----------");
            MockRestLogger.log(actions.andReturn().getResponse());
            log.info("----- END REQUEST/RESPONSE LOGGING -----");
        }

    }
}
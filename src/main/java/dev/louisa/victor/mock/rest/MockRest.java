package dev.louisa.victor.mock.rest;

import com.fasterxml.jackson.core.type.TypeReference;
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

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@RequiredArgsConstructor
public class MockRest {
    private final MockMvc mockMvc;
    private final ObjectMapper mapper;

    // =========================
    // === HTTP verb methods ===
    // =========================
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

    // =============================
    // === Nested RequestBuilder ===
    // =============================
    @RequiredArgsConstructor
    public static class RequestBuilder {
        private final MockMvc mockMvc;
        private final ObjectMapper mapper;
        private final MockHttpServletRequestBuilder request;

        private HttpStatus expectedStatus = null;
        private final Map<String, String> expectedResponseHeaders = new HashMap<>();
        private RequestPostProcessor userPostProcessor;

        // --- request configuration ---
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
            this.userPostProcessor = req -> {
                req.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
                return req;
            };
            return this;
        }

        public RequestBuilder expectResponseStatus(HttpStatus status) {
            this.expectedStatus = status;
            return this;
        }

        public RequestBuilder expectResponseHeader(String name, String value) {
            expectedResponseHeaders.put(name, value);
            return this;
        }

        // --- intermediate termination: execute ---
        public ResponseBuilder send() throws Exception {
            ResultActions actions = (userPostProcessor != null)
                    ? mockMvc.perform(request.with(userPostProcessor))
                    : mockMvc.perform(request);

            logDetails(actions);

            if (expectedStatus != null) {
                actions.andExpect(status().is(expectedStatus.value()));
            }

            expectedResponseHeaders.forEach((k, v) -> assertResponseHeader(actions, k, v));

            return new ResponseBuilder(actions.andReturn(), mapper); // allow optional chaining
        }

        // --- helpers ---
        private void assertResponseHeader(ResultActions actions, String key, String value) {
            try {
                actions.andExpect(r1 -> {
                    String header = r1.getResponse().getHeader(key);
                    if (header == null) {
                        throw new AssertionError("Expected header '" + key + "' but not found");
                    }
                    if (!header.equals(value)) {
                        throw new AssertionError("Expected header '" + key + "' value '" + value + "', but was: " + header);
                    }
                });
            } catch (Exception e) {
                throw new AssertionError(e);
            }
        }

        private void logDetails(ResultActions actions) {
            log.info("------------ REQUEST DETAILS -----------");
            MockRestLogger.log(actions.andReturn().getRequest());
            log.info("----------- RESPONSE DETAILS -----------");
            MockRestLogger.log(actions.andReturn().getResponse());
            log.info("----- END REQUEST/RESPONSE LOGGING -----");
        }


        // ==============================
        // === Nested ResponseBuilder ===
        // ==============================
        @RequiredArgsConstructor
        public static class ResponseBuilder {
            private final MvcResult result;
            private final ObjectMapper mapper;

            // --- optional: fetch and deserialize response body ---
            public <T> T andReturn(Class<T> type) throws Exception {
                return parseResponse(type, null);
            }

            public <T> T andReturn(TypeReference<T> typeRef) throws Exception {
                return parseResponse(null, typeRef);
            }

            // --- internal helper ---
            private <T> T parseResponse(Class<T> clazz, TypeReference<T> typeRef) throws Exception {
                String content = result.getResponse().getContentAsString();
                try {
                    return typeRef != null
                            ? mapper.readValue(content, typeRef)
                            : mapper.readValue(content, clazz);
                } catch (Exception e) {
                    String typeName = (clazz != null) ? clazz.getSimpleName() : "generic type";
                    throw new AssertionError("Failed to parse response body to " + typeName + ": " + content, e);
                }
            }
        }
    }
}
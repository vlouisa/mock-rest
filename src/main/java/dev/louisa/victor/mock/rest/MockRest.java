package dev.louisa.victor.mock.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.louisa.victor.mock.rest.config.RequestConfigurer;
import dev.louisa.victor.mock.rest.config.ResponseExpectation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

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

        private final List<RequestConfigurer> configurers = new ArrayList<>();
        private final List<ResponseExpectation> expectations = new ArrayList<>();

        // --- request configuration ---
        public RequestBuilder body(Object body) {
            configurers.add(RequestConfigurer.body(mapper, body));
            return this;
        }

        public RequestBuilder withRequestHeader(String name, String value) {
            configurers.add(RequestConfigurer.header(name, value));
            return this;
        }

        public RequestBuilder withJwt(String token) {
            configurers.add(RequestConfigurer.jwt(token));
            return this;
        }

        public RequestBuilder expectResponseStatus(HttpStatus status) {
            expectations.add(ResponseExpectation.expectedStatus(status));
            return this;
        }

        public RequestBuilder expectResponseHeader(String name, String value) {
            expectations.add(ResponseExpectation.expectedHeader(name, value));
            return this;
        }

        // --- intermediate termination: execute ---
        public ResponseBuilder send() throws Exception {
            final ResultActions actions = mockMvc.perform(configuredRequest());
            expectations.forEach(exp -> exp.apply(actions));
            
            MockRestLogger.log(actions);
            return new ResponseBuilder(actions.andReturn(), mapper);
        }

        private MockHttpServletRequestBuilder configuredRequest() {
            configurers
                    .forEach(cfg -> cfg.apply(request));
            return request;
        }

    }


    // ==============================
    // === Nested ResponseBuilder ===
    // ==============================
    @RequiredArgsConstructor
    public static class ResponseBuilder {
        private final MvcResult result;
        private final ObjectMapper mapper;

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
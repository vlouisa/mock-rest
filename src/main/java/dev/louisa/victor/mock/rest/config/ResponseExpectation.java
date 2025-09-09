package dev.louisa.victor.mock.rest.config;

import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@FunctionalInterface
public interface ResponseExpectation {
    void apply(ResultActions actions);

    static ResponseExpectation expectedStatus(HttpStatus status) {
        return actions -> andExpect(status, actions);
    }

    private static void andExpect(HttpStatus status, ResultActions actions) {
        try {
            actions.andExpect(MockMvcResultMatchers.status().is(status.value()));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    static ResponseExpectation expectedHeader(String name, String value) {
        return actions -> andExpect(name, value, actions);
    }

    private static void andExpect(String name, String value, ResultActions actions) {
        try {
            actions.andExpect(result -> {
                String actual = result.getResponse().getHeader(name);
                if (actual == null) {
                    throw new AssertionError("Expected header '" + name + "' but it was not found");
                }
                if (!actual.equals(value)) {
                    throw new AssertionError("Expected header '" + name + "' value '" + value + "', but was '" + actual + "'");
                }
            });
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}

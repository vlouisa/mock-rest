package dev.louisa.victor.mock.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Slf4j
public class MockRestLogger {

    public static void log(MockHttpServletRequest built) {
        try {
            log.info("HTTP " + built.getMethod() + " " + built.getRequestURI());

            // Query string
            if (built.getQueryString() != null) {
                log.info("   ? " + built.getQueryString());
            }

            // Headers
            log.info("   Headers:");
            built.getHeaderNames().asIterator().forEachRemaining(name ->
                    log.info("      " + name + ": " + built.getHeader(name))
            );

            // Body (if JSON or text)
            log.info("   Body:");
            if (built.getContentLength() > 0) {
                String body = new String(Objects.requireNonNull(built.getContentAsByteArray()), StandardCharsets.UTF_8);
                log.info("      " + body);
            }

        } catch (Exception e) {
            log.error("Failed to log request: " + e.getMessage());
        }
    }

    public static void log(MockHttpServletResponse response) {
        log.info("HTTP STATUS {}", response.getStatus());

        // Headers
        log.info("   Headers:");
        for (String name : response.getHeaderNames()) {
            log.info("      " + name + ": " + response.getHeader(name));
        }

        // Body (if JSON/text)
        String body = null;
        try {
            body = response.getContentAsString();
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage(), e);
        }


        log.info("   Body:");
        if (body != null && !body.isBlank()) {
            log.info("      " + body);
        }
    }
}

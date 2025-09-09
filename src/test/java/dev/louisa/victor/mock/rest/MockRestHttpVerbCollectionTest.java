package dev.louisa.victor.mock.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import dev.louisa.victor.mock.rest.dto.User;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.OK;

class MockRestHttpVerbCollectionTest extends BaseTest<List<User>> {

    @Test
    void shouldCallTheGetAllEndpointOnTheController() throws Exception {
        when(controller.getAllUsers())
                .thenReturn(response(OK, List.of(GRISWOLD, GUYBRUSH)));

        mockRest
                .get("/api/v1/users")
                .send();

        verify(controller).getAllUsers();
    }

    @Test
    void shouldReturnExpectedResponseBody() throws Exception {
        when(controller.getAllUsers())
                .thenReturn(response(OK, List.of(GRISWOLD, GUYBRUSH)));

        var response = mockRest
                .get("/api/v1/users")
                .send()
                .andReturn(new TypeReference<List<User>>(){});

        assertThat(response)
                .containsExactly(GRISWOLD, GUYBRUSH);
    }
}

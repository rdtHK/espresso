package rdthk.espresso;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.EmptyStackException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static rdthk.espresso.Request.Method.GET;
import static rdthk.espresso.Request.Method.POST;

public class MethodMiddlewareTest {
    private Request request;
    private Response response;
    private Middleware.Stack stack;

    @BeforeEach
    void beforeEach() {
        request = mock(Request.class);
        response = mock(Response.class);
        stack = new Middleware.Stack(request);
    }

    @Test
    void testHandleRequest() {
        when(request.getMethod()).thenReturn(GET);
        Middleware middleware = new MethodMiddleware(GET, (req, s) -> response);
        assertEquals(response, middleware.handleRequest(request, stack));
    }

    @Test
    void testMethodDidNotMatch() {
        when(request.getMethod()).thenReturn(GET);
        Middleware middleware = new MethodMiddleware(POST, (req, s) -> response);
        assertThrows(EmptyStackException.class, () -> middleware.handleRequest(request, stack));
    }
}

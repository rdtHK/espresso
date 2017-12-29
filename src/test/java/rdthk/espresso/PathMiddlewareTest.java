package rdthk.espresso;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.EmptyStackException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PathMiddlewareTest {


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
    void testEmptyPath() {
        Middleware path = new PathMiddleware("", (req, s) -> response);

        when(request.getPath()).thenReturn("");
        assertEquals(response, path.handleRequest(request, stack));

        when(request.getPath()).thenReturn("other");
        assertThrows(EmptyStackException.class, () -> path.handleRequest(request, stack));
    }

    @Test
    void testStaticPath() {
        Middleware path = new PathMiddleware("path", (req, s) -> response);

        when(request.getPath()).thenReturn("path");
        assertEquals(response, path.handleRequest(request, stack));

        when(request.getPath()).thenReturn("other");
        assertThrows(EmptyStackException.class, () -> path.handleRequest(request, stack));
    }

    @Test
    void testPathWildcard() {
        Middleware path = new PathMiddleware("*", (req, s) -> response);

        when(request.getPath()).thenReturn("");
        assertEquals(response, path.handleRequest(request, stack));

        when(request.getPath()).thenReturn("other");
        assertEquals(response, path.handleRequest(request, stack));
    }

    @Test
    void testPathWithParameter() {
        Middleware path = new PathMiddleware(":name", (req, s) -> response);

        when(request.getPath()).thenReturn("value");
        assertEquals(response, path.handleRequest(request, stack));
        verify(request).putParameter("name", "value");
    }
}


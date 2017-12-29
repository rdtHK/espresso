package rdthk.espresso;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.EmptyStackException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

public class MiddlewareStackTest {
    Request request;
    Response response;
    Middleware.Stack stack;

    @BeforeEach
    void beforeEach() {
        request = mock(Request.class);
        response = mock(Response.class);
        stack = new Middleware.Stack(request);
    }

    @Test
    void testEmptyMiddleware() {
        assertThrows(EmptyStackException.class, stack::pop);
    }

    @Test
    void testSingleMiddleware() {
        stack.push((req, chain) -> response);
        assertEquals(response, stack.pop());
    }

    @Test
    void testMultipleMiddleware() {
        stack.push((req, chain) -> response);
        stack.push((req, chain) -> chain.pop());
        assertEquals(response, stack.pop());
    }

    @Test
    void testExceptionPropagation() {
        stack.push((req, chain) -> {
            throw new UnsupportedOperationException();
        });
        assertThrows(UnsupportedOperationException.class, stack::pop);
    }

    @Test
    void testMockitoEquals() {
        Response a = mock(Response.class);
        Response b = mock(Response.class);
        assertEquals(a, a);
        assertNotEquals(a, b);
    }
}

package rdthk.espresso;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.EmptyStackException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static rdthk.espresso.Request.Method.*;

public class GroupMiddlewareTest {
    private Request request;
    private Response response;
    private Middleware.Stack stack;
    private GroupMiddleware group;

    @BeforeEach
    void beforeEach() {
        request = mock(Request.class);
        response = mock(Response.class);
        stack = new Middleware.Stack(request);
        group = new GroupMiddleware();
    }

    @Test
    void testEmptyGroup() {
        assertThrows(EmptyStackException.class, () -> group.handleRequest(request, stack));
    }

    @Test
    void testMatchingWithMultipleRoutes() {
        setRequest(GET, "right");
        group.get("wrong", (req) -> null);
        group.get("right", (req) -> response);
        assertEquals(response, group.handleRequest(request, stack));
    }

    @Test
    void testNoMatches() {
        setRequest(GET, "right");
        group.get("1", (req) -> null);
        group.get("2", (req) -> null);
        assertThrows(EmptyStackException.class, () -> group.handleRequest(request, stack));
    }

    @Test
    void testUse() {
        group.use((req, stack) -> response);
        assertEquals(response, group.handleRequest(request, stack));
    }

    @Test
    void testGet() {
        group.get("match", (req) -> response);

        setRequest(GET, "match");
        assertEquals(response, group.handleRequest(request, stack));

        setRequest(POST, "match");
        assertThrows(EmptyStackException.class, () -> group.handleRequest(request, stack));
    }

    @Test
    void testPost() {
        group.post("match", (req) -> response);

        setRequest(POST, "match");
        assertEquals(response, group.handleRequest(request, stack));

        setRequest(GET, "match");
        assertThrows(EmptyStackException.class, () -> group.handleRequest(request, stack));
    }

    @Test
    void testPut() {
        group.put("match", (req) -> response);

        setRequest(PUT, "match");
        assertEquals(response, group.handleRequest(request, stack));

        setRequest(GET, "match");
        assertThrows(EmptyStackException.class, () -> group.handleRequest(request, stack));
    }

    @Test
    void testDelete() {
        group.delete("match", (req) -> response);

        setRequest(DELETE, "match");
        assertEquals(response, group.handleRequest(request, stack));

        setRequest(GET, "match");
        assertThrows(EmptyStackException.class, () -> group.handleRequest(request, stack));
    }

    @Test
    void testPatch() {
        group.patch("match", (req) -> response);

        setRequest(PATCH, "match");
        assertEquals(response, group.handleRequest(request, stack));

        setRequest(GET, "match");
        assertThrows(EmptyStackException.class, () -> group.handleRequest(request, stack));
    }

    @Test
    void testSubRouteGroup() {
        setRequest(GET, "match");

        group.group((r) -> {
            r.get("match", (req) -> response);
        });

        assertEquals(response, group.handleRequest(request, stack));
    }

    @Test
    void testSubRouteGroupWithPrefix() {
        setRequest(GET, "prefix/match");

        group.group("prefix/", (r) -> {
            r.get("match", (req) -> response);
        });

        assertEquals(response, group.handleRequest(request, stack));
    }

    @Test
    void testUsePrefix() {
        group = new GroupMiddleware("prefix");
        group.use((req, stack) -> response);

        setRequest(GET, "prefix");
        assertEquals(response, group.handleRequest(request, stack));

        setRequest(POST, "prefix-and-more");
        assertEquals(response, group.handleRequest(request, stack));

        group = new GroupMiddleware("other-prefix");
        group.use((req, stack) -> response);
        assertThrows(EmptyStackException.class, () -> group.handleRequest(request, stack));
    }

    @Test
    void testAllWithPrefix() {
        setRequest(GET, "prefix");

        group = new GroupMiddleware("prefix");
        group.all((req) -> response);
        assertEquals(response, group.handleRequest(request, stack));

        group = new GroupMiddleware("other-prefix");
        group.all((req) -> response);
        assertThrows(EmptyStackException.class, () -> group.handleRequest(request, stack));

        setRequest(GET, "prefix-and-some-more");
        group = new GroupMiddleware("prefix");
        group.all((req) -> response);
        assertEquals(response, group.handleRequest(request, stack));
    }

    private void setRequest(Request.Method get, String path) {
        when(request.getMethod()).thenReturn(get);
        when(request.getPath()).thenReturn(path);
    }
}

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
        notFound();
    }

    @Test
    void testMultipleRoutes() {
        setRequest(GET, "right");
        group.get("wrong", (req) -> null);
        group.get("right", (req) -> response);
        found();
    }

    @Test
    void testNoMatches() {
        setRequest(GET, "right");
        group.get("1", (req) -> null);
        group.get("2", (req) -> null);
        notFound();
    }

    @Test
    void testGet() {
        group.get("match", (req) -> response);

        setRequest(GET, "match");
        found();

        setRequest(GET, "wrong-path");
        notFound();

        setRequest(POST, "match");
        notFound();
    }

    @Test
    void testPost() {
        group.post("match", (req) -> response);

        setRequest(POST, "match");
        found();

        setRequest(GET, "match");
        notFound();
    }

    @Test
    void testPut() {
        group.put("match", (req) -> response);

        setRequest(PUT, "match");
        found();

        setRequest(GET, "match");
        notFound();
    }

    @Test
    void testDelete() {
        group.delete("match", (req) -> response);

        setRequest(DELETE, "match");
        found();

        setRequest(GET, "match");
        notFound();
    }

    @Test
    void testPatch() {
        group.patch("match", (req) -> response);

        setRequest(PATCH, "match");
        found();

        setRequest(GET, "match");
        notFound();
    }

    @Test
    void testSubRouteGroup() {
        setRequest(GET, "match");

        group.group((r) -> {
            r.get("match", (req) -> response);
        });

        found();
    }

    @Test
    void testSubRouteGroupWithPrefix() {
        setRequest(GET, "prefix/match");

        group.group("prefix/", (r) -> {
            r.get("match", (req) -> response);
        });

        found();
    }

    @Test
    void testAllWithPrefix() {

        setRequest(GET, "prefix");
        group = new GroupMiddleware("prefix");
        group.all((req) -> response);
        found();

        setRequest(GET, "prefix-and-some-more");
        group = new GroupMiddleware("prefix");
        group.all((req) -> response);
        found();

        setRequest(GET, "prefix");
        group = new GroupMiddleware("other-prefix");
        group.all((req) -> response);
        notFound();
    }

    @Test
    void testAllWithController() {
        group.all((req) -> response);

        setRequest(GET, "path");
        found();

        setRequest(POST, "other-path");
        found();
    }

    @Test
    void testAllWithMiddleware() {
        group.all((req, s) -> response);

        setRequest(GET, "path");
        found();

        setRequest(PUT, "other-path");
        found();
    }

    @Test
    void testAllWithPathAndController() {
        group.all("path", (req) -> response);

        setRequest(GET, "path");
        found();

        setRequest(GET, "path-and-more");
        notFound();

        setRequest(GET, "other");
        notFound();
    }

    @Test
    void testAllWithPathAndMiddleware() {
        group.all("path", (req, s) -> response);

        setRequest(GET, "path");
        found();

        setRequest(GET, "path-and-more");
        notFound();

        setRequest(GET, "other");
        notFound();
    }

    @Test
    void testAllWithPathAndPrefix() {
        group = new GroupMiddleware("prefix/");
        group.all("path", (req) -> response);

        setRequest(GET, "prefix/path");
        found();
    }

    private void found() {
        assertEquals(response, group.handleRequest(request, stack));
    }


    private void notFound() {
        assertThrows(EmptyStackException.class, () -> group.handleRequest(request, stack));
    }

    private void setRequest(Request.Method get, String path) {
        when(request.getMethod()).thenReturn(get);
        when(request.getPath()).thenReturn(path);
    }
}

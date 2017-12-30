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
    void testAllWithMiddleware() {
        group.all((req, s) -> response);

        setRequest(GET, "foo");
        found();

        setRequest(PUT, "bar");
        found();
    }

    @Test
    void testAllWithController() {
        group.all((req) -> response);

        setRequest(GET, "foo");
        found();

        setRequest(POST, "bar");
        found();
    }


    @Test
    void testAllWithMiddlewareAndPrefix() {
        group = new GroupMiddleware("prefix");
        group.all((req, s) -> response);

        setRequest(GET, "prefix");
        found();

        setRequest(GET, "prefix-and-more");
        found();

        group = new GroupMiddleware("wrong-prefix");
        group.all((req, s) -> response);

        setRequest(GET, "prefix");
        notFound();
    }

    @Test
    void testAllWithControllerAndPrefix() {
        group = new GroupMiddleware("prefix");
        group.all((req) -> response);

        setRequest(GET, "prefix");
        found();

        setRequest(GET, "prefix-and-more");
        found();

        group = new GroupMiddleware("wrong-prefix");
        group.all((req) -> response);

        setRequest(GET, "prefix");
        notFound();
    }

    @Test
    void testAllWithPathAndMiddleware() {
        group.all("right", (req, s) -> response);

        setRequest(GET, "right");
        found();

        setRequest(GET, "right-and-more");
        notFound();

        setRequest(GET, "wrong");
        notFound();
    }

    @Test
    void testAllWithPathAndController() {
        group.all("right", (req) -> response);

        setRequest(GET, "right");
        found();

        setRequest(GET, "right-and-more");
        notFound();

        setRequest(GET, "wrong");
        notFound();
    }

    @Test
    void testAllWithPathAndMiddlewareAndPrefix() {
        group = new GroupMiddleware("prefix/");
        group.all("right", (req, s) -> response);

        setRequest(GET, "right");
        notFound();

        setRequest(GET, "prefix/right");
        found();
    }

    @Test
    void testAllWithPathAndControllerAndPrefix() {
        group = new GroupMiddleware("prefix/");
        group.all("right", (req) -> response);

        setRequest(GET, "right");
        notFound();

        setRequest(GET, "prefix/right");
        found();
    }

    @Test
    void testGetWithMiddleware() {
        group.get((req, s) -> response);

        setRequest(GET, "right");
        found();

        setRequest(POST, "right");
        notFound();
    }

    @Test
    void testGetWithController() {
        group.get((req) -> response);

        setRequest(GET, "right");
        found();

        setRequest(POST, "right");
        notFound();
    }


    @Test
    void testGetWithMiddlewareAndPrefix() {
        group = new GroupMiddleware("prefix");
        group.get((req, s) -> response);

        setRequest(GET, "prefix");
        found();

        setRequest(GET, "prefix-and-more");
        found();

        group = new GroupMiddleware("wrong-prefix");
        group.get((req, s) -> response);

        setRequest(GET, "prefix");
        notFound();
    }

    @Test
    void testGetWithControllerAndPrefix() {
        group = new GroupMiddleware("prefix");
        group.get((req) -> response);

        setRequest(GET, "prefix");
        found();

        setRequest(GET, "prefix-and-more");
        found();

        group = new GroupMiddleware("wrong-prefix");
        group.get((req) -> response);

        setRequest(GET, "prefix");
        notFound();
    }

    @Test
    void testGetWithPathAndMiddleware() {
        group.get("right", (req, s) -> response);

        setRequest(GET, "right");
        found();

        setRequest(GET, "wrong");
        notFound();

        setRequest(POST, "right");
        notFound();
    }

    @Test
    void testGetWithPathAndController() {
        group.get("right", (req) -> response);

        setRequest(GET, "right");
        found();

        setRequest(GET, "wrong");
        notFound();

        setRequest(POST, "right");
        notFound();
    }

    @Test
    void testGetWithPathAndMiddlewareAndPrefix() {
        group = new GroupMiddleware("prefix/");
        group.get("right", (req, s) -> response);

        setRequest(GET, "right");
        notFound();

        setRequest(GET, "prefix/right");
        found();
    }

    @Test
    void testGetWithPathAndControllerAndPrefix() {
        group = new GroupMiddleware("prefix/");
        group.get("right", (req) -> response);

        setRequest(GET, "right");
        notFound();

        setRequest(GET, "prefix/right");
        found();
    }

    @Test
    void testPostWithMiddleware() {
        group.post((req, s) -> response);

        setRequest(POST, "right");
        found();

        setRequest(PUT, "right");
        notFound();
    }

    @Test
    void testPostWithController() {
        group.post((req) -> response);

        setRequest(POST, "right");
        found();

        setRequest(PUT, "right");
        notFound();
    }

    @Test
    void testPostWithMiddlewareAndPrefix() {
        group = new GroupMiddleware("prefix");
        group.post((req, s) -> response);

        setRequest(POST, "prefix");
        found();

        setRequest(POST, "prefix-and-more");
        found();

        group = new GroupMiddleware("wrong-prefix");
        group.post((req, s) -> response);

        setRequest(POST, "prefix");
        notFound();
    }

    @Test
    void testPostWithControllerAndPrefix() {
        group = new GroupMiddleware("prefix");
        group.post((req) -> response);

        setRequest(POST, "prefix");
        found();

        setRequest(POST, "prefix-and-more");
        found();

        group = new GroupMiddleware("wrong-prefix");
        group.post((req) -> response);

        setRequest(POST, "prefix");
        notFound();
    }

    @Test
    void testPostWithPathAndMiddleware() {
        group.post("right", (req, s) -> response);

        setRequest(POST, "right");
        found();

        setRequest(POST, "wrong");
        notFound();

        setRequest(PUT, "right");
        notFound();
    }

    @Test
    void testPostWithPathAndController() {
        group.post("right", (req) -> response);

        setRequest(POST, "right");
        found();

        setRequest(POST, "wrong");
        notFound();

        setRequest(PUT, "right");
        notFound();
    }

    @Test
    void testPostWithPathAndMiddlewareAndPrefix() {
        group = new GroupMiddleware("prefix/");
        group.post("right", (req, s) -> response);

        setRequest(POST, "right");
        notFound();

        setRequest(POST, "prefix/right");
        found();
    }

    @Test
    void testPostWithPathAndControllerAndPrefix() {
        group = new GroupMiddleware("prefix/");
        group.post("right", (req) -> response);

        setRequest(POST, "right");
        notFound();

        setRequest(POST, "prefix/right");
        found();
    }

    @Test
    void testPutWithPathAndController() {
        group.put("right", (req) -> response);

        setRequest(PUT, "right");
        found();

        setRequest(PUT, "wrong");
        notFound();

        setRequest(GET, "right");
        notFound();
    }

    @Test
    void testDeleteWithPathAndController() {
        group.delete("right", (req) -> response);

        setRequest(DELETE, "right");
        found();

        setRequest(DELETE, "wrong");
        notFound();

        setRequest(GET, "right");
        notFound();
    }

    @Test
    void testPatchWithPathAndController() {
        group.patch("right", (req) -> response);

        setRequest(PATCH, "right");
        found();

        setRequest(PATCH, "wrong");
        notFound();

        setRequest(GET, "right");
        notFound();
    }

    @Test
    void testSubRouteGroup() {
        group.group((r) -> {
            r.get("right", (req) -> response);
        });

        setRequest(GET, "right");
        found();
    }

    @Test
    void testSubRouteGroupWithPrefix() {
        group.group("prefix/", (r) -> {
            r.get("right", (req) -> response);
        });

        setRequest(GET, "right");
        notFound();

        setRequest(GET, "prefix/right");
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

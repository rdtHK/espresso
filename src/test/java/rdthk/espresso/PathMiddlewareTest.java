package rdthk.espresso;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

public class PathMiddlewareTest {
    private HttpServletRequest request;
    private HttpServletResponse response;
    private Middleware.Chain chain;

    @BeforeEach
    void beforeEach() {
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        chain = mock(Middleware.Chain.class);
    }

    @Test
    void testUsesBasePath() {
        when(request.getPathInfo()).thenReturn("/foo/bar");
        when(request.getAttribute("base-path")).thenReturn("/foo");

        Middleware middleware = new PathMiddleware("/bar");
        middleware.handleRequest(request, response, chain);

        verify(chain).next();
    }

    @Test
    void testMergesParentParams() {
        Map<String, String> parentParams = new HashMap<>();
        parentParams.put("a", "b");

        Map<String, String> resultParams = new HashMap<>();
        resultParams.put("a", "b");
        resultParams.put("foo", "bar");

        when(request.getPathInfo()).thenReturn("/bar");
        when(request.getAttribute("path-parameters")).thenReturn(parentParams);

        Middleware middleware = new PathMiddleware("/:foo");
        middleware.handleRequest(request, response, chain);

        verify(request).setAttribute("path-parameters", parentParams);
        verify(request).setAttribute("path-parameters", resultParams);
    }

    @Test
    void testUpdatesBasePath() {
        when(request.getPathInfo()).thenReturn("/foo/bar");
        when(request.getAttribute("base-path")).thenReturn("/foo");

        Middleware middleware = new PathMiddleware("/bar");
        middleware.handleRequest(request, response, chain);

        verify(request).setAttribute("base-path", "/foo/bar");
        verify(request).setAttribute("base-path", "/foo");
    }

    @Test
    void testStaticPath() {
        when(request.getPathInfo()).thenReturn("/foo");

        Middleware middleware = new PathMiddleware("/foo");
        middleware.handleRequest(request, response, chain);

        verify(chain).next();
    }

    @Test
    void testWrongPath() {
        when(request.getPathInfo()).thenReturn("/foo");

        Middleware middleware = new PathMiddleware("/bar");
        middleware.handleRequest(request, response, chain);

        verify(chain, never()).next();
    }

    @Test
    void testPathWildcard() {
        Middleware middleware = new PathMiddleware("*");

        when(request.getPathInfo()).thenReturn("/foo");
        middleware.handleRequest(request, response, chain);

        when(request.getPathInfo()).thenReturn("/bar");
        middleware.handleRequest(request, response, chain);

        verify(chain, times(2)).next();
    }

    @Test
    void testPathWithParameter() {
        Middleware middleware = new PathMiddleware("/:foo");

        when(request.getPathInfo()).thenReturn("/bar");
        middleware.handleRequest(request, response, chain);

        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("foo", "bar");
        verify(request).setAttribute("path-parameters", parameters);
    }
}


package rdthk.espresso;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class RouterTest {
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
    void testEmptyRouter() {
        Router router = new Router();
        router.handleRequest(request, response, chain);
        verify(chain).next();
    }

    @Test
    void testStopWhenResponseIsCommitted() {
        when(response.isCommitted()).thenReturn(true);
        Router router = new Router();
        router.handleRequest(request, response, chain);
        verify(chain, never()).next();
    }

    @Test
    void testWithMiddleware() {
        AtomicInteger i = new AtomicInteger();
        Middleware middleware = (req, res, chain) -> {
            i.getAndIncrement();
            chain.next();
        };

        Router router = new Router();
        router.add(middleware);
        router.add(middleware);
        router.handleRequest(request, response, chain);

        assertEquals(2, i.get());
    }

    @Test
    void testRouteMatches() {
        when(request.getMethod()).thenReturn("GET");
        when(request.getPathInfo()).thenReturn("/foo");

        Router router = new Router();
        Controller controller = mock(Controller.class);
        router.add("GET", "/foo", controller);
        router.handleRequest(request, response, chain);
        verify(controller).handleRequest(request, response);
    }
}

package rdthk.espresso;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.mockito.Mockito.*;


public class MethodMiddlewareTest {

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
    void testHandleRequest() {
        when(request.getMethod()).thenReturn("GET");
        Middleware middleware = new MethodMiddleware("GET");
        middleware.handleRequest(request, response, chain);
        verify(chain).next();
    }

    @Test
    void testMethodDidNotMatch() {
        when(request.getMethod()).thenReturn("POST");
        Middleware middleware = new MethodMiddleware("GET");
        middleware.handleRequest(request, response, chain);
        verify(chain, never()).next();
    }
}

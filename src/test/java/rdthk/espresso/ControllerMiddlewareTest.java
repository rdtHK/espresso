package rdthk.espresso;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ControllerMiddlewareTest {
    private HttpServletRequest request;
    private HttpServletResponse response;
    private Controller controller;
    private Middleware.Chain chain;

    @BeforeEach
    void beforeEach() {
        controller = mock(Controller.class);
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        chain = mock(Middleware.Chain.class);
    }


    @Test
    void testHandleRequest() throws IOException {
        Middleware middleware = new ControllerMiddleware(controller);
        middleware.handleRequest(request, response, chain);
        verify(controller).handleRequest(request, response);
    }
}

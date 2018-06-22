package rdthk.espresso;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ControllerMiddlewareTest {
    private Request request;
    private Middleware.Stack stack;
    private Controller controller;

    @BeforeEach
    void beforeEach() {
        request = mock(Request.class);
        stack = new Middleware.Stack(request);
        controller = mock(Controller.class);
    }


    @Test
    void testHandleRequest() {
        Middleware middleware = new ControllerMiddleware(controller);
        middleware.handleRequest(request, stack);
        verify(controller).handleRequest(request);
    }
}

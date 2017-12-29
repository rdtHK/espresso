package rdthk.espresso;

import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ControllerMiddlewareTest {
    @Test
    void testHandleRequest() {
        Request request = mock(Request.class);
        Middleware.Stack stack = new Middleware.Stack(request);
        Controller controller = mock(Controller.class);
        Middleware middleware = new ControllerMiddleware(controller);
        middleware.handleRequest(request, stack);
        verify(controller).handleRequest(request);
    }
}

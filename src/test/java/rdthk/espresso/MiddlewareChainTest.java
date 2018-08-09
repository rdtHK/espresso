package rdthk.espresso;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class MiddlewareChainTest {

    private HttpServletResponse response;
    private HttpServletRequest request;

    @BeforeEach
    void beforeEach() {
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
    }

    @Test
    void testNextWithNoItems() {
        Middleware.Chain chain = new Middleware.Chain(request, response, new ArrayList<>());
        chain.next();
    }

    @Test
    void testNextWithSingleItem() {
        Middleware middleware = mock(Middleware.class);
        List<Middleware> items = new ArrayList<>();
        items.add(middleware);

        Middleware.Chain chain = new Middleware.Chain(request, response, items);
        chain.next();
        verify(middleware).handleRequest(request, response, chain);
    }
}

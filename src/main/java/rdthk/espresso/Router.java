package rdthk.espresso;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

public class Router implements Middleware {
    private final List<Middleware> children = new ArrayList<>();

    public void add(Middleware middleware) {
        children.add(middleware);
    }

    public void add(String method, String path, Middleware middleware) {
        Router router = new Router();
        router.add(new MethodMiddleware(method));
        router.add(new PathMiddleware(path));
        router.add(middleware);

        children.add(router);
    }

    public void add(String method, String path, Controller controller) {
        Router router = new Router();
        router.add(new MethodMiddleware(method));
        router.add(new PathMiddleware(path));
        router.add(new ControllerMiddleware(controller));

        children.add(router);
    }

    @Override
    public void handleRequest(HttpServletRequest request, HttpServletResponse response, Chain parentChain) {
        Chain newChain = new Chain(request, response, children);
        newChain.next();

        if (!response.isCommitted()) {
            parentChain.next();
        }
    }
}

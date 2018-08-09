package rdthk.espresso;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

public class RouterMiddleware implements Middleware {
    private final List<Middleware> children = new ArrayList<>();

    private void use(Middleware middleware) {
    }

    private void use(String method, String path, Middleware middleware) {
    }

    private void add(String method, String path, Controller controller) {
    }

    @Override
    public void handleRequest(HttpServletRequest request, HttpServletResponse response, Chain chain) {

    }
}

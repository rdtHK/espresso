package rdthk.espresso;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MethodMiddleware implements Middleware {
    private final String method;

    public MethodMiddleware(String method) {
        this.method = method;
    }

    @Override
    public void handleRequest(HttpServletRequest request, HttpServletResponse response, Chain chain) {
        if (request.getMethod().equals(method)) {
            chain.next();
        }
    }
}

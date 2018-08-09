package rdthk.espresso;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ControllerMiddleware implements Middleware {
    private final Controller controller;

    public ControllerMiddleware(Controller controller) {
        this.controller = controller;
    }

    @Override
    public void handleRequest(HttpServletRequest request, HttpServletResponse response, Chain chain) {
        controller.handleRequest(request, response);
    }
}

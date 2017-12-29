package rdthk.espresso;

public class ControllerMiddleware implements Middleware {
    private final Controller controller;

    public ControllerMiddleware(Controller controller) {
        this.controller = controller;
    }

    @Override
    public Response handleRequest(Request request, Stack stack) {
        return controller.handleRequest(request);
    }
}

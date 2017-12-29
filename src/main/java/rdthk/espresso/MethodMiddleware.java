package rdthk.espresso;

public class MethodMiddleware implements Middleware {
    private final Middleware child;
    private final Request.Method method;

    public MethodMiddleware(Request.Method method, Middleware child) {
        this.method = method;
        this.child = child;
    }

    @Override
    public Response handleRequest(Request request, Stack stack) {
        if (method.equals(request.getMethod())) {
            return child.handleRequest(request, stack);
        }
        return stack.pop();
    }
}

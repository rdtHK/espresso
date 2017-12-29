package rdthk.espresso;

public interface Middleware {
    Response handleRequest(Request request, Stack stack);

    class Stack {
        private final Request request;
        private final java.util.Stack<Middleware> stack;

        public Stack(Request request) {
            this.request = request;
            stack = new java.util.Stack<>();
        }

        public Response pop() {
            Middleware middleware = stack.pop();
            return middleware.handleRequest(request, this);
        }

        public void push(Middleware middleware) {
            stack.push(middleware);
        }
    }
}


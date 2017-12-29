package rdthk.espresso;

import java.util.Map;

public class PathMiddleware implements Middleware {
    private final Path path;
    private final Middleware child;

    public PathMiddleware(String path, Middleware child) {
        this.path = new Path(path);
        this.child = child;
    }

    @Override
    public Response handleRequest(Request request, Stack stack) {
        Path.MatchResult result = path.match(request.getPath());

        if (result.matches) {
            for (Map.Entry<String, String> parameter: result.parameters.entrySet()) {
                request.putParameter(parameter.getKey(), parameter.getValue());
            }

            return child.handleRequest(request, stack);
        } else {
            return stack.pop();
        }
    }
}

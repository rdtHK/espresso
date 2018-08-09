package rdthk.espresso;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public class PathMiddleware implements Middleware {
    private final Path path;

    public PathMiddleware(String path) {
        this.path = new Path(path);
    }

    @Override
    public void handleRequest(HttpServletRequest request, HttpServletResponse response, Chain chain) {
        String basePath = (String) request.getAttribute("base-path");

        if (basePath == null) {
            basePath = "";
        }

        Path.MatchResult result = path.match(request.getPathInfo().substring(basePath.length()));

        if (result.matches) {
            Map<String, String> parameters = result.parameters;
            Map<String, String> parentParameters = (Map<String, String>) request.getAttribute("path-parameters");

            if (parentParameters != null) {
                parameters.putAll(parentParameters);
            }

            request.setAttribute("base-path", basePath + result.text);
            request.setAttribute("path-parameters", parameters);
            chain.next();
            request.setAttribute("base-path", basePath);
            request.setAttribute("path-parameters", parentParameters);
        }
    }
}

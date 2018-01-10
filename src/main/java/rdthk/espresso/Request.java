package rdthk.espresso;

import java.util.HashMap;
import java.util.Map;

public class Request {
    private Method method;
    private String path;
    private Map<String, String> parameters;

    public Request(Method method, String path) {
        this.method = method;
        this.path = path;
        parameters = new HashMap<>();
    }

    public Method getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public void putParameter(String name, String value) {
        parameters.put(name, value);
    }

    enum Method {
        GET,
        POST,
        PUT,
        DELETE,
        PATCH
    }
}

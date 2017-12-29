package rdthk.espresso;

import java.util.ArrayList;
import java.util.List;

import static rdthk.espresso.Request.Method.*;

public class GroupMiddleware implements Middleware {
    private final List<Middleware> children = new ArrayList<>();
    private final String prefix;

    public interface Configuration {
        void configure(GroupMiddleware group);
    }

    public GroupMiddleware(String prefix) {
        this.prefix = prefix;
    }

    public GroupMiddleware() {
        this.prefix = "";
    }

    @Override
    public Response handleRequest(Request request, Middleware.Stack stack) {
        for (int i = children.size() - 1; i >= 0; i--) {
            stack.push(children.get(i));
        }

        return stack.pop();
    }

    public void all(Controller action) {
        children.add(new PathMiddleware(prefix + "*", new ControllerMiddleware(action)));
    }

    public void get(String path, Controller action) {
        children.add(route(GET, path, action));
    }

    public void post(String path, Controller action) {
        children.add(route(POST, path, action));
    }

    public void put(String path, Controller action) {
        children.add(route(PUT, path, action));
    }

    public void delete(String path, Controller action) {
        children.add(route(DELETE, path, action));
    }

    public void patch(String path, Controller action) {
        children.add(route(PATCH, path, action));
    }

    public void group(Configuration configuration) {
        GroupMiddleware child = new GroupMiddleware(fullPath(""));
        configuration.configure(child);
        children.add(child);
    }

    public void group(String prefix, Configuration configuration) {
        GroupMiddleware child = new GroupMiddleware(fullPath(prefix));
        configuration.configure(child);
        children.add(child);
    }

    private String fullPath(String path) {
        return prefix + path;
    }

    private Middleware route(Request.Method method, String path, Controller action) {
        return new MethodMiddleware(method, new PathMiddleware(fullPath(path), new ControllerMiddleware(action)));
    }
}

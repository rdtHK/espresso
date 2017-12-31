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

    public void all(Middleware middleware) {
        all("*", middleware);
    }

    public void all(Controller action) {
        all("*", action);
    }

    public void all(String path, Middleware middleware) {
        children.add(new PathMiddleware(fullPath(path), middleware));
    }

    public void all(String path, Controller action) {
        all(path, new ControllerMiddleware(action));
    }

    public void get(Middleware middleware) {
        get("*", middleware);
    }

    public void get(Controller action) {
        get("*", action);
    }

    public void get(String path, Middleware middleware) {
        route(GET, path, middleware);
    }

    public void get(String path, Controller action) {
        get(path, new ControllerMiddleware(action));
    }

    public void post(Middleware middleware) {
        post("*", middleware);
    }

    public void post(Controller action) {
        post("*", action);
    }

    public void post(String path, Middleware middleware) {
        route(POST, path, middleware);
    }

    public void post(String path, Controller action) {
        post(path, new ControllerMiddleware(action));
    }

    public void put(Middleware middleware) {
        put("*", middleware);
    }

    public void put(Controller action) {
        put("*", action);
    }

    public void put(String path, Middleware middleware) {
        route(PUT, path, middleware);
    }

    public void put(String path, Controller action) {
        put(path, new ControllerMiddleware(action));
    }

    public void patch(Middleware middleware) {
        patch("*", middleware);
    }

    public void patch(Controller action) {
        patch("*", action);
    }

    public void patch(String path, Middleware middleware) {
        route(PATCH, path, middleware);
    }

    public void patch(String path, Controller action) {
        patch(path, new ControllerMiddleware(action));
    }

    public void delete(Middleware middleware) {
        delete("*", middleware);
    }

    public void delete(Controller action) {
        delete("*", action);
    }

    public void delete(String path, Middleware middleware) {
        route(DELETE, path, middleware);
    }

    public void delete(String path, Controller action) {
        delete(path, new ControllerMiddleware(action));
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

    private void route(Request.Method method, String path, Middleware middleware) {
        children.add(new MethodMiddleware(method, new PathMiddleware(fullPath(path), middleware)));
    }
}

package rdthk.espresso;

import net.bytebuddy.implementation.bytecode.Throw;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.EmptyStackException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static rdthk.espresso.Request.Method.*;

public class GroupMiddlewareTest {
    private String methodName;
    private boolean withPrefix;
    private boolean withPath;
    private boolean withController;
    private Request request;
    private Response response;
    private Middleware.Stack stack;
    private GroupMiddleware group;

    @BeforeEach
    void beforeEach() {
        methodName = "";
        withPrefix = false;
        withPath = false;
        withController = false;

        request = mock(Request.class);
        response = mock(Response.class);
        stack = new Middleware.Stack(request);
        group = new GroupMiddleware();
    }

    @Test
    void testEmptyGroup() {
        assertFailure();
    }

    @Test
    void testMultipleRoutes() {
        withRequest(GET, "right");
        group.get("wrong", (req) -> null);
        group.get("right", (req) -> response);
        assertSuccess();
    }

    @Test
    void testNoMatches() {
        withRequest(GET, "right");
        group.get("1", (req) -> null);
        group.get("2", (req) -> null);
        assertFailure();
    }

    @Test
    void testRouteMethods() {
        String[] methods = {
                "all",
                "get",
                "post",
                "put",
                "patch",
                "delete"
        };

        try {
            for (String method : methods) {
                test(method, false);
                test(method, true);
                testWithPrefix(method, false);
                testWithPrefix(method, true);
                testWithPath(method, false);
                testWithPath(method, true);
                testWithPathAndPrefix(method, false);
                testWithPathAndPrefix(method, true);
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(getContext(), e);
        }
    }

    private void test(String name, boolean withController) {
        beforeEach();
        this.methodName = name;
        this.withController = withController;

        Request.Method rightMethod = name.equals("all") ? GET : Request.Method.valueOf(name.toUpperCase());
        Request.Method wrongMethod = name.equals("all") ? null : (name.equals("get") ? POST : GET);

        callMethod(name, withController);

        withRequest(rightMethod, "right");
        assertSuccess();

        if (wrongMethod != null) {
            withRequest(wrongMethod, "right");
            assertFailure();
        }
    }

    private void testWithPrefix(String name, boolean withController) {
        beforeEach();
        this.methodName = name;
        this.withPrefix = true;
        this.withController = withController;
        group = new GroupMiddleware("prefix");

        Request.Method method = name.equals("all") ? GET : Request.Method.valueOf(name.toUpperCase());

        callMethod(name, withController);

        withRequest(method, "prefix-right");
        assertSuccess();

        withRequest(method, "wrong");
        assertFailure();
    }

    private void testWithPath(String name, boolean withController) {
        beforeEach();
        this.methodName = name;
        this.withPath = true;
        this.withController = withController;

        Request.Method method = name.equals("all") ? GET : Request.Method.valueOf(name.toUpperCase());

        callMethod(name, "right", withController);

        withRequest(method, "right");
        assertSuccess();

        withRequest(method, "right-and-more");
        assertFailure();

        withRequest(method, "wrong");
        assertFailure();
    }

    private void testWithPathAndPrefix(String name, boolean withController) {
        beforeEach();
        this.methodName = name;
        this.withPrefix = true;
        this.withPath = true;
        this.withController = withController;

        group = new GroupMiddleware("prefix/");

        Request.Method method = name.equals("all") ? GET : Request.Method.valueOf(name.toUpperCase());

        callMethod(name, "right", withController);

        withRequest(method, "prefix/right");
        assertSuccess();

        withRequest(method, "right");
        assertFailure();
    }

    @Test
    void testSubRouteGroup() {
        group.group((r) -> {
            r.get("right", (req) -> response);
        });

        withRequest(GET, "right");
        assertSuccess();
    }

    @Test
    void testSubRouteGroupWithPrefix() {
        group.group("prefix/", (r) -> {
            r.get("right", (req) -> response);
        });

        withRequest(GET, "right");
        assertFailure();

        withRequest(GET, "prefix/right");
        assertSuccess();
    }

    @Test
    void testCallMethod() {
        callMethod("all", false);
        callMethod("all", true);
        assertThrows(RuntimeException.class, () -> callMethod("madeup", true));
        assertThrows(RuntimeException.class, () -> callMethod("madeup", false));

        callMethod("all", "right", false);
        callMethod("all", "right", true);
        assertThrows(RuntimeException.class, () -> callMethod("madeup", "right", true));
        assertThrows(RuntimeException.class, () -> callMethod("madeup", "right", false));
    }

    private void callMethod(String name, boolean withController) {
        try {
            Method method = GroupMiddleware.class.getMethod(name, withController ? Controller.class : Middleware.class);

            if (withController) {
                method.invoke(group, (Controller) (req) -> response);
            } else {
                method.invoke(group, (Middleware) (req, s) -> response);
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            String message = "Missing " + name + "(" + (withController ? "Controlller)" : "Middleware)");
            throw new RuntimeException(message, e);
        }
    }

    private void callMethod(String name, String path, boolean withController) {
        try {
            Method method = GroupMiddleware.class.getMethod(name, String.class, withController ? Controller.class : Middleware.class);

            if (withController) {
                method.invoke(group, path, (Controller) (req) -> response);
            } else {
                method.invoke(group, path, (Middleware) (req, s) -> response);
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            String message = "Missing " + name + "(String, " + (withController ? "Controlller)" : "Middleware)");
            throw new RuntimeException(message, e);
        }
    }

    private void assertSuccess() {
        assertEquals(response, group.handleRequest(request, stack), getContext());
    }

    private void assertFailure() {

        assertThrows(EmptyStackException.class, () -> group.handleRequest(request, stack), getContext());
    }

    private String getContext() {
        return "On " + methodName + "(" +
                (withPath ? "String," : "") +
                (withController ? "Controller" : "Middleware") + ")" +
                (withPrefix ? " with a prefix" : "");
    }

    private void withRequest(Request.Method get, String path) {
        when(request.getMethod()).thenReturn(get);
        when(request.getPath()).thenReturn(path);
    }
}

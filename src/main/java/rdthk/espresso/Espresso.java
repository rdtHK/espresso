package rdthk.espresso;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;

public class Espresso {
    private Server server;
    private Router router = new Router();

    public void start(int port) {
        server = new Server(port);
        server.setHandler(new RequestHandler());

        try {
            server.start();
            server.join();
        } catch (Exception e) {
            throw new EspressoException(e);
        }
    }

    public void stop() {
        if (server != null) {
            try {
                server.stop();
            } catch (Exception e) {
                throw new EspressoException(e);
            }
        }
    }

    public void add(Middleware middleware) {
        router.add(middleware);
    }

    public void add(String method, String path, Middleware middleware) {
        router.add(method, path, middleware);
    }

    public void add(String method, String path, Controller controller) {
        router.add(method, path, controller);
    }

    private class RequestHandler extends AbstractHandler {
        @Override
        public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            router.handleRequest(request, response, new Middleware.Chain(request, response, new ArrayList<>()));
            baseRequest.setHandled(true);
        }
    }
}

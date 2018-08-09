package rdthk.espresso;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface Middleware {
    void handleRequest(HttpServletRequest request, HttpServletResponse response, Chain chain);

    class Chain {
        private final HttpServletRequest request;
        private final HttpServletResponse response;
        private final List<Middleware> items;
        int i = 0;

        public Chain(HttpServletRequest request, HttpServletResponse response, List<Middleware> items) {
            this.request = request;
            this.response = response;
            this.items = items;
        }

        public void next() {
            if (i < items.size()) {
                Middleware middleware = items.get(i++);
                middleware.handleRequest(request, response, this);
            }
        }
    }
}


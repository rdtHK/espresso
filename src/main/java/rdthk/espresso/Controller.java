package rdthk.espresso;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface Controller {
    void handleRequest(HttpServletRequest request, HttpServletResponse response);
}

package rdthk.espresso;

public interface Request {
    Method getMethod();
    String getPath();

    void putParameter(String name, String value);

    enum Method {
        GET,
        POST,
        PUT,
        DELETE,
        PATCH
    }
}

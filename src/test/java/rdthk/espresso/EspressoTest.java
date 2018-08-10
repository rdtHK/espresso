package rdthk.espresso;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

class EspressoTest {
    @Test
    void testStartStop() throws IOException {
        Espresso espresso = new Espresso();

        espresso.add("GET", "/foo", (req, res) -> {
            try {
                res.getWriter().write("foobar");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        (new Thread(() -> {
            espresso.start(8080);
        })).start();

        assertEquals("foobar", get("http://localhost:8080/foo"));

        espresso.stop();
    }

    private String get(String url) throws IOException {
        URL urlObj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) urlObj.openConnection();
        con.setRequestMethod("GET");
        con.setReadTimeout(1000);
        con.setConnectTimeout(1000);
        return new String(con.getInputStream().readAllBytes());
    }


}
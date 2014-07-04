package rule.example;

import static org.junit.Assert.assertEquals;

import java.net.HttpURLConnection;
import java.net.URL;

import com.github.stefanbirkner.serverrule.HttpServer;
import org.junit.Rule;
import org.junit.Test;

public class ServerTest {
    @Rule
    public final HttpServer server = new HttpServer();

    @Test
    public void checksStatusCodeOkForOurFile() throws Exception {
        URL url = new URL("http://localhost:8080/test.html");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.connect();
        assertEquals(200, connection.getResponseCode());
    }
}

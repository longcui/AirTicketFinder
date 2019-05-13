package travel.wizz;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.Assert.*;

public class WizzWeekendsPriceTest {

    @Test
    public void testGet() {
        HttpRequest httpRequest = HttpRequest.newBuilder(URI.create("https://www.google.no"))
                .GET().build();
        try {
            HttpResponse<String> httpResponse = HttpClient.newHttpClient()
                    .send(httpRequest, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, httpResponse.statusCode());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testPush() throws ParseException {
        HttpRequest httpRequest = HttpRequest.newBuilder(URI.create("http://httpbin.org/post"))
                .POST(HttpRequest.BodyPublishers.ofString("test"))
                .build();
        try {
            HttpResponse<String> httpResponse = HttpClient.newHttpClient()
                    .send(httpRequest, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, httpResponse.statusCode());
            String body = httpResponse.body();
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject)jsonParser.parse(body);
            assertNotNull(jsonObject);
            assertEquals("test", jsonObject.get("data"));
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

}
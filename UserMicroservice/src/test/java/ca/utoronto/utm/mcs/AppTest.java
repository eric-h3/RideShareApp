package ca.utoronto.utm.mcs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;

/**
 * Please write your tests in this class. 
 */
 
public class AppTest {

    final static String API_URL = "http://localhost:8001";

    private static HttpResponse<String> sendRequest(String endpoint, String reqBody) throws IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + endpoint))
                .POST(BodyPublishers.ofString(reqBody))
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }
    @Test
    public void registerValid() throws JSONException, IOException, InterruptedException {
        JSONObject confirmReq = new JSONObject()
                .put("name", "hello")
                .put("email", "hello@mail.com")
                .put("password", "somepassword");
    HttpResponse<String> confirmRes = sendRequest("/user/register", confirmReq.toString());
    assertEquals(HttpURLConnection.HTTP_OK, confirmRes.statusCode());
    }

    @Test
    public void registerInvalid() throws JSONException, IOException, InterruptedException {
        JSONObject confirmReq = new JSONObject()
                .put("name", "Mike")
                .put("password", "somepassword");
    HttpResponse<String> confirmRes = sendRequest("/user/register", confirmReq.toString());
    assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, confirmRes.statusCode());
    }

    @Test
    public void loginValid() throws JSONException, IOException, InterruptedException {
        JSONObject confirmReq = new JSONObject()
                .put("email", "hello@mail.com")
                .put("password", "somepassword");
    HttpResponse<String> confirmRes = sendRequest("/user/login", confirmReq.toString());
    assertEquals(HttpURLConnection.HTTP_OK, confirmRes.statusCode());
    }

    @Test
    public void loginInvalid() throws JSONException, IOException, InterruptedException {
        JSONObject confirmReq = new JSONObject()
                .put("email", "mike@mail.com");
    HttpResponse<String> confirmRes = sendRequest("/user/login", confirmReq.toString());
    assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, confirmRes.statusCode());
    }
}

package ca.utoronto.utm.mcs;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

/**
 * Please write your tests in this class. 
 */
 
public class AppTest {

    final static String API_URL = "http://localhost:8000";

    private static HttpResponse<String> sendRequest(String endpoint) throws IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + endpoint))
                .GET()
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @Test
    public void nevigationValid() throws JSONException, IOException, InterruptedException{
        HttpResponse<String> confirmRes = sendRequest("/location/navigation/1?passengerUid=4");
        assertEquals(HttpURLConnection.HTTP_OK, confirmRes.statusCode());
    }

    @Test
    public void nevigationInvalid() throws JSONException, IOException, InterruptedException{
        HttpResponse<String> confirmRes = sendRequest("/location/navigation/");
        assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, confirmRes.statusCode());
    }

    @Test
    public void NearbyValid() throws JSONException, IOException, InterruptedException{
        HttpResponse<String> confirmRes = sendRequest("/location/nearbyDriver/1?radius=1");
        assertEquals(HttpURLConnection.HTTP_OK, confirmRes.statusCode());
    }

    @Test
    public void NearbyInvalid() throws JSONException, IOException, InterruptedException{
        HttpResponse<String> confirmRes = sendRequest("/location/nearbyDriver/");
        assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, confirmRes.statusCode());
    }
}

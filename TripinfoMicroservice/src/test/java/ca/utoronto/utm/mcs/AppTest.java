package ca.utoronto.utm.mcs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    final static String API_URL = "http://localhost:8002";

    private static HttpResponse<String> sendRequest(String endpoint, String method, String reqBody) throws IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + endpoint))
                .method(method, HttpRequest.BodyPublishers.ofString(reqBody))
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @Test
    public void requestSuccess() throws JSONException, IOException, InterruptedException {
        JSONObject confirmReq = new JSONObject()
                .put("uid", "10")
                .put("radius", 15);
        HttpResponse<String> confirmRes = sendRequest("/trip/request", "POST", confirmReq.toString());
        assertEquals(HttpURLConnection.HTTP_OK, confirmRes.statusCode());
    }

    @Test
    public void requestFail() throws JSONException, IOException, InterruptedException {
        JSONObject confirmReq = new JSONObject()
                .put("uid", "343")
                .put("radius", -1);
        HttpResponse<String> confirmRes = sendRequest("/trip/request", "POST", confirmReq.toString());
        assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, confirmRes.statusCode());
    }

    @Test
    public void confirmSuccess() throws JSONException, IOException, InterruptedException {
        JSONObject confirmReq = new JSONObject()
                .put("driver", "2")
                .put("passenger", "10")
                .put("startTime", 123456);
        HttpResponse<String> confirmRes = sendRequest("/trip/confirm", "POST", confirmReq.toString());
        assertEquals(HttpURLConnection.HTTP_OK, confirmRes.statusCode());
    }

    @Test
    public void confirmFail() throws JSONException, IOException, InterruptedException {
        JSONObject confirmReq = new JSONObject()
                .put("driver", 1)
                .put("passenger", 2)
                .put("startTime", "123456");
        HttpResponse<String> confirmRes = sendRequest("/trip/confirm", "POST", confirmReq.toString());
        assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, confirmRes.statusCode());
    }

    @Test
    public void tripSuccess() throws JSONException, IOException, InterruptedException {
        JSONObject confirmReq = new JSONObject()
                .put("distance", 5)
                .put("endTime", 5)
                .put("timeElapsed", 5)
                .put("totalCost", "23452");
        HttpResponse<String> confirmRes = sendRequest("/trip/507f1f77bcf86cd799439011", "PATCH", confirmReq.toString());
        assertEquals(HttpURLConnection.HTTP_OK, confirmRes.statusCode());
    }

    @Test
    public void tripFail() throws JSONException, IOException, InterruptedException {
        JSONObject confirmReq = new JSONObject()
                .put("distance", 5)
                .put("endTime", 5)
                .put("timeElapsed", 5);
        HttpResponse<String> confirmRes = sendRequest("/trip/507f1f77bcf86cd799439011", "PATCH", confirmReq.toString());
        assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, confirmRes.statusCode());
    }

    @Test
    public void driverSuccess() throws JSONException, IOException, InterruptedException {
        HttpResponse<String> confirmRes = sendRequest("/trip/driver/507f1f77bcf86cd799439011", "GET", "");
        assertEquals(HttpURLConnection.HTTP_OK, confirmRes.statusCode());
    }

    @Test
    public void driverFail() throws JSONException, IOException, InterruptedException {
        HttpResponse<String> confirmRes = sendRequest("/trip/driver", "GET", "");
        assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, confirmRes.statusCode());
    }

    @Test
    public void passengerSucess() throws JSONException, IOException, InterruptedException {
        HttpResponse<String> confirmRes = sendRequest("/trip/passenger/507f1f77bcf86cd799439011", "GET", "");
        assertEquals(HttpURLConnection.HTTP_OK, confirmRes.statusCode());
    }

    @Test
    public void passengerFail() throws JSONException, IOException, InterruptedException {
        HttpResponse<String> confirmRes = sendRequest("/trip/driver", "GET", "");
        assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, confirmRes.statusCode());
    }

    @Test
    public void drivetimeSuccess() throws JSONException, IOException, InterruptedException {
        HttpResponse<String> confirmRes = sendRequest("/trip/driverTime/507f1f77bcf86cd799439011", "GET", "");
        assertEquals(HttpURLConnection.HTTP_OK, confirmRes.statusCode());
    }

    @Test
    public void drivetimeFail() throws JSONException, IOException, InterruptedException {
        HttpResponse<String> confirmRes = sendRequest("/trip/driverTime/507f1f77bcf86cd799439011/23423", "GET", "");
        assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, confirmRes.statusCode());
    }
}
    
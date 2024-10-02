package ca.utoronto.utm.mcs;

/** 
 * Everything you need in order to send and recieve httprequests to 
 * other microservices is given here. Do not use anything else to send 
 * and/or recieve http requests from other microservices. Any other 
 * imports are fine.
 */
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;

import com.sun.net.httpserver.HttpExchange;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Request extends Endpoint {

    final static String LOCATION_URL = "http://locationmicroservice:8000";
    /**
     * POST /trip/request
     * @body uid, radius
     * @return 200, 400, 404, 500
     * Returns a list of drivers within the specified radius 
     * using location microservice. List should be obtained
     * from navigation endpoint in location microservice
     */

    @Override
    public void handlePost(HttpExchange r) throws IOException,JSONException {
        String body = Utils.convert(r.getRequestBody());
		JSONObject deserialized = new JSONObject(body);

        String[] splitUrl = r.getRequestURI().getPath().split("/");
		if (splitUrl.length != 3) {
			this.sendStatus(r, 400);
			return;
		}
        if (!deserialized.has("uid") || !deserialized.has("radius")) {
            this.sendStatus(r, 400);
            return;
        }
        if (deserialized.get("uid").getClass() != String.class) {
            this.sendStatus(r, 400);
            return;
        }
        if (deserialized.get("radius").getClass() != Integer.class) {
            this.sendStatus(r, 400);
            return;
        }
        String uid = deserialized.getString("uid");
        int radius = deserialized.getInt("radius");

        if (radius < 0) {
            this.sendStatus(r, 400);
            return;
        }
        // Check if drivers available in the radius
        String location_endpoint = "/location/nearbyDriver/" + uid + "?radius=" + radius;

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(LOCATION_URL + location_endpoint))
                .GET()
                .build();
        HttpResponse<String> response;
        
         try {
             response = client.send(request, HttpResponse.BodyHandlers.ofString());
             if (response.statusCode() != 200) {
                 this.sendStatus(r, 404);
                 return;
             }

             Pattern p = Pattern.compile("\"[0-9]+\"");
             Matcher matcher = p.matcher(response.body());
             List<String> driverList = new ArrayList<String>();
             while (matcher.find()){
                 driverList.add(matcher.group(0).substring(1, matcher.group(0).length() - 1));
             }

             JSONObject res = new JSONObject();
             res.put("status", "OK");
             res.put("data", driverList);

             this.sendResponse(r, res, 200);
         } catch (Exception e1) {
             e1.printStackTrace();
             this.sendStatus(r, 500);
             return;
         }
    }
}

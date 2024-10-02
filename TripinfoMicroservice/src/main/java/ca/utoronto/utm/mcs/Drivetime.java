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

import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.views.DocumentView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


import java.io.IOException;

public class Drivetime extends Endpoint {

    final static String LOCATION_URL = "http://locationmicroservice:8000";

    /**
     * GET /trip/driverTime/:_id
     * @param _id
     * @return 200, 400, 404, 500
     * Get time taken to get from driver to passenger on the trip with
     * the given _id. Time should be obtained from navigation endpoint
     * in location microservice.
     */

    @Override
    public void handleGet(HttpExchange r) throws IOException, JSONException {
        
        String[] splitUrl = r.getRequestURI().getPath().split("/");
        if (splitUrl.length != 4) {
            this.sendStatus(r, 400);
            return;
        }
        String _idString = splitUrl[3];
        // Check if _id has mongo ObjectId format, send 400 otherwise
        if (!ObjectId.isValid(_idString)) {
            this.sendStatus(r, 400);
            return;
        }

        String driverUid = "", passengerUid = "";

        try {
            Document curser = this.dao.findbyId(_idString);
            if (curser != null) {
                driverUid = curser.get("driver").toString();
                passengerUid = curser.get("passenger").toString();
            } else {
                this.sendStatus(r, 404);
            }
            
            
        }catch (Exception e) {
            e.printStackTrace();
            this.sendStatus(r, 500);
        }
        
        String location_endpoint = "/location/navigation/" + driverUid + "?passengerUid=" + passengerUid;

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(LOCATION_URL + location_endpoint))
                .GET()
                .build();
        HttpResponse<String> response;
        
        

        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                this.sendStatus(r, response.statusCode());
                return;
            }

            String arrival_time = "";
            Pattern p = Pattern.compile("\"\\btotal_time\\b\":[0-9]+");
            Matcher matcher = p.matcher(response.body());
            if (matcher.find()){
                arrival_time = matcher.group(0).substring(13);
            }
            
            JSONObject res = new JSONObject();
            JSONObject data = new JSONObject();
            data.put("arrival_time", arrival_time);
            res.put("data", data);
            res.put("status", "OK");

            this.sendResponse(r, res, 200);
        } catch (Exception e1) {
            e1.printStackTrace();
            this.sendStatus(r, 500);
            return;
        }
        
    }
}

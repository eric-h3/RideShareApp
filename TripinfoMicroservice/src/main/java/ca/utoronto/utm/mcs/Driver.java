package ca.utoronto.utm.mcs;

import com.mongodb.client.FindIterable;
import com.sun.net.httpserver.HttpExchange;

import org.bson.Document;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class Driver extends Endpoint {

    /**
     * GET /trip/driver/:uid
     * @param uid
     * @return 200, 400, 404
     * Get all trips driver with the given uid has.
     */

    @Override
    public void handleGet(HttpExchange r) throws IOException, JSONException {
        // TODO
        
        String[] splitUrl = r.getRequestURI().getPath().split("/");
        if (splitUrl.length != 4) {
            this.sendStatus(r, 400);
            return;
        }

        String uidString = splitUrl[3];
        
        try {
            Document passenger = this.dao.getDriver(uidString);
            if (passenger == null) {
                this.sendStatus(r, 404);
            }
        } catch (Exception e) {
            e.printStackTrace();
            this.sendStatus(r, 500);
            return;
        }

        try {
            FindIterable<Document> cursor = this.dao.getDriverTrips(uidString);

            if (cursor != null) {

                JSONObject res = new JSONObject();
                JSONObject data = new JSONObject();
                res.put("status", "OK");
                data.put("trips", Utils.findIterableToJSONArray(cursor));
                res.put("data", data);

                this.sendResponse(r, res, 200);
            }
            this.sendStatus(r, 404);
            return;

        } catch (Exception e) {
            e.printStackTrace();
            this.sendStatus(r, 500);
        }
    }
}

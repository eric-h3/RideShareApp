package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;

import org.bson.Document;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;

public class Confirm extends Endpoint {

    /**
     * POST /trip/confirm
     * @body driver, passenger, startTime
     * @return 200, 400
     * Adds trip info into the database after trip has been requested.
     */

    @Override
    public void handlePost(HttpExchange r) throws IOException, JSONException {
        // TODO
        String body = Utils.convert(r.getRequestBody());
		JSONObject deserialized = new JSONObject(body);

        String[] splitUrl = r.getRequestURI().getPath().split("/");
        if (splitUrl.length != 3) {
            this.sendStatus(r, 400);
            return;
        }
        if (!deserialized.has("driver") || !deserialized.has("passenger") || !deserialized.has("startTime")) {
            this.sendStatus(r, 400);
            return;
        }
        if (deserialized.get("driver").getClass() != String.class) {
            this.sendStatus(r, 400);
            return;
        }
        if (deserialized.get("passenger").getClass() != String.class) {
            this.sendStatus(r, 400);
            return;
        }
        if (deserialized.get("startTime").getClass() != Integer.class) {
            this.sendStatus(r, 400);
            return;
        }

        String driver_uid = deserialized.getString("driver");
        String passenger_uid = deserialized.getString("passenger");
        int startTime = deserialized.getInt("startTime");

        try {
            this.dao.postConfirm(driver_uid, passenger_uid, startTime);
        }catch (Exception e) {
            e.printStackTrace();
            this.sendStatus(r, 500);
            return;
        }

        try {
            Document curser = this.dao.getDriver(driver_uid);
            if (curser != null) {
                JSONObject var = new JSONObject();
                JSONObject data = new JSONObject();
                JSONObject id = new JSONObject();
                id.put("$oid", curser.get("_id"));
                data.put("_id", id);
                var.put("data", data);
                var.put("status", "OK");

                this.sendResponse(r, var, 200);
                return;
            }
            this.sendStatus(r, 404);
        } catch (Exception e) {
            e.printStackTrace();
            this.sendStatus(r, 500);
        }

    }
}

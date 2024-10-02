package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class Trip extends Endpoint {

    /**
     * PATCH /trip/:_id
     * @param _id
     * @body distance, endTime, timeElapsed, totalCost
     * @return 200, 400, 404
     * Adds extra information to the trip with the given id when the 
     * trip is done. 
     */

    @Override
    public void handlePatch(HttpExchange r) throws IOException, JSONException {
        // TODO
        // check if request url isn't malformed
        String[] splitUrl = r.getRequestURI().getPath().split("/");
        if (splitUrl.length != 3) {
            this.sendStatus(r, 400);
            return;
        }

        // check if id is valid ObjectId
        ObjectId objectId = null;
        String trip_id = splitUrl[2];
        if (!ObjectId.isValid(trip_id)) {
            this.sendStatus(r, 400);
            return;
        }

        String body = Utils.convert(r.getRequestBody());
		JSONObject deserialized = new JSONObject(body);

        Integer distance = null;
        Integer endTime = null;
        Integer timeElapsed = null;
        String totalCost = null;

        // check what values are present
        if (!deserialized.has("distance") || !deserialized.has("endTime") || 
            !deserialized.has("timeElapsed") || !deserialized.has("totalCost")) {
            this.sendStatus(r, 400);
            return;
        }
        if (deserialized.get("distance").getClass() != Integer.class) {
            this.sendStatus(r, 400);
            return;
        }
        distance = deserialized.getInt("distance");
        if (deserialized.get("endTime").getClass() != Integer.class) {
            this.sendStatus(r, 400);
            return;
        }
        endTime = deserialized.getInt("endTime");
        if (deserialized.get("timeElapsed").getClass() != Integer.class) {
            this.sendStatus(r, 400);
            return;
        }
        timeElapsed = deserialized.getInt("timeElapsed");
        if (deserialized.get("totalCost").getClass() != String.class) {
            this.sendStatus(r, 400);
            return;
        }
        totalCost = deserialized.getString("totalCost");

        // if the trip Id is not in database status code should be 404
        Document cursor = this.dao.findbyId(trip_id);
        try {
            if (cursor != null) {
                objectId = new ObjectId(trip_id);
                this.dao.patchTrip(objectId, distance, endTime, timeElapsed, totalCost);
                this.sendStatus(r, 200);
            }
            this.sendStatus(r, 404); 
        } catch (Exception e) {
            e.printStackTrace();
            this.sendStatus(r, 500);
        }
    }
}

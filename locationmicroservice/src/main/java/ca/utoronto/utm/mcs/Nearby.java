package ca.utoronto.utm.mcs;

import java.io.IOException;
import org.json.*;
import org.neo4j.driver.*;
import com.sun.net.httpserver.HttpExchange;
import org.neo4j.driver.Record;

public class Nearby extends Endpoint {
    
    /**
     * GET /location/nearbyDriver/:uid?radius=:radius
     * @param uid, radius
     * @return 200, 400, 404, 500
     * Get drivers that are within a certain radius around a user.
     */

    @Override
    public void handleGet(HttpExchange r) throws IOException, JSONException {
        // TODO
        String[] temParams = r.getRequestURI().toString().split("/");
        if (temParams.length != 4 || temParams[3].isEmpty()) {
            this.sendStatus(r, 400);
            return;
        }

        String[] params = temParams[3].split("\\?");
        if (params.length != 2 || params[0].isEmpty()) {
            this.sendStatus(r, 400);
            return;
        }

        try{
            String uid = params[0];
            String[] temRadius = params[1].split("=");
            float radius = Float.parseFloat(temRadius[1]);
            Result userLocation = this.dao.getUserLocationByUid(uid);
            float longitude = 0, latitude = 0;

            if (userLocation.hasNext()) {

                Record user = userLocation.next();
                longitude = user.get("n.longitude").asFloat();
                latitude = user.get("n.latitude").asFloat();

            } else {
                this.sendStatus(r, 404);
            }

            Result result = this.dao.getDriverByRadius(longitude, latitude, radius);
            if (result.hasNext()) {
                JSONObject res = new JSONObject();
                JSONObject data = new JSONObject();
                while (result.hasNext()){
                    Record user = result.next();
                    String driverUid = user.get("n.uid").asString();
                    Double driverLongitude = user.get("n.longitude").asDouble();
                    Double driverLatitude = user.get("n.latitude").asDouble();
                    String street = user.get("n.street").asString();


                    JSONObject driverInfo = new JSONObject();
                    data.put(driverUid, driverInfo);
                    driverInfo.put("longitude", driverLongitude);
                    driverInfo.put("latitude", driverLatitude);
                    driverInfo.put("street", street);
                }

                res.put("status", "OK");
                res.put("data", data);

                this.sendResponse(r, res, 200);
            } else {
                this.sendStatus(r, 404);
            }



        } catch (Exception e) {
            e.printStackTrace();
            this.sendStatus(r, 500);
        }
    }
}

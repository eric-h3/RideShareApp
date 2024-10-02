package ca.utoronto.utm.mcs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.*;
import com.sun.net.httpserver.HttpExchange;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;

public class Navigation extends Endpoint {
    
    /**
     * GET /location/navigation/:driverUid?passengerUid=:passengerUid
     * @param driverUid, passengerUid
     * @return 200, 400, 404, 500
     * Get the shortest path from a driver to passenger weighted by the
     * travel_time attribute on the ROUTE_TO relationship.
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

        try {
            String driverUid = params[0];
            String[] temPassengerUid = params[1].split("=");
            String passengerUid = temPassengerUid[1];
            Result driverLocation = this.dao.getUserLocationByUid(driverUid);
            Result passengerLocation = this.dao.getUserLocationByUid(passengerUid);
            String driverStreet = "", passengerStreet = "";

            if (driverLocation.hasNext()) {

                Record user = driverLocation.next();
                driverStreet = user.get("n.street").asString();

            } else {
                this.sendStatus(r, 404);
            }

            if (passengerLocation.hasNext()) {

                Record user = passengerLocation.next();
                passengerStreet = user.get("n.street").asString();

            } else {
                this.sendStatus(r, 404);
            }

            Result allPaths = this.dao.getPathsByRoads(driverStreet, passengerStreet);
            List<List<Object>> names = new ArrayList<List<Object>>();
            List<List<Object>> times = new ArrayList<List<Object>>();
            List<List<Object>> traffics = new ArrayList<List<Object>>();
            if (allPaths.hasNext()) {
                while (allPaths.hasNext()){
                    Record user = allPaths.next();
                    names.add(user.get("[x in nodes(p) | x.name]").asList());
                    times.add(user.get("[y in relationships(p) | y.travel_time]").asList());
                    traffics.add(user.get("[x in nodes(p) | x.has_traffic]").asList());
                }
            }else {
                this.sendStatus(r, 404);
            }

            int shortestRouteIndex = 0;
            int shortestTime = 0;
            for (int i = 0; i < times.size(); i++) {
                int time = 0;
                for (int j = 0; j < times.get(i).size(); j++) {
                    time += Integer.parseInt(times.get(i).get(j).toString());
                    if (j == times.get(i).size() - 1 || time < shortestTime) {
                        shortestTime = time;
                        shortestRouteIndex = i;
                    }

                }
            }

            JSONObject res = new JSONObject();
            JSONObject data = new JSONObject();
            List<JSONObject> route = new ArrayList<JSONObject>();
            data.put("total_time", shortestTime);

            for (int i = 0; i < names.get(shortestRouteIndex).size(); i++){
                JSONObject routeInfo = new JSONObject();
                routeInfo.put("street", names.get(shortestRouteIndex).get(i).toString());
                if (i == 0) {
                    routeInfo.put("time", 0);
                } else {
                    routeInfo.put("time", Integer.parseInt(times.get(shortestRouteIndex).get(i - 1).toString()));
                }
                routeInfo.put("has_traffic", Boolean.parseBoolean(traffics.get(shortestRouteIndex).get(i).toString()));
                route.add(routeInfo);
            }

            data.put("route", route);
            res.put("status", "OK");
            res.put("data", data);

            this.sendResponse(r, res, 200);
        } catch (Exception e) {
            e.printStackTrace();
            this.sendStatus(r, 500);
        }
    }
}

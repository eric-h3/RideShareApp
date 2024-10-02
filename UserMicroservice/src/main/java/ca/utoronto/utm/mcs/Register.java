package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.sql.*;

public class Register extends Endpoint {

    /**
     * POST /user/register
     * @body name, email, password
     * @return 200, 400, 500
     * Register a user into the system using the given information.
     */

    @Override
    public void handlePost(HttpExchange r) throws IOException, JSONException {
        // TODO
        String body = Utils.convert(r.getRequestBody());
		JSONObject deserialized = new JSONObject(body);
        String email = null;
		String name = null;
		String password = null;
        if (!deserialized.has("email") || !deserialized.has("password") || !deserialized.has("name")) {
            this.sendStatus(r, 400);
            return;
        }
        if (deserialized.get("email").getClass() != String.class) {
            this.sendStatus(r, 400);
            return;
        }
        email = deserialized.getString("email");
        if (deserialized.get("name").getClass() != String.class) {
            this.sendStatus(r, 400);
            return;
        }
        name = deserialized.getString("name");
        if (deserialized.get("password").getClass() != String.class) {
            this.sendStatus(r, 400);
            return;
        }
        password = deserialized.getString("password");

        // make query to check if user with given email exists, return 500 if error
        ResultSet rs1;
        boolean resultHasNext;
        try {
            rs1 = this.dao.getUsersFromEmail(email);
            resultHasNext = rs1.next();
        } 
        catch (SQLException e) {
            e.printStackTrace();
            this.sendStatus(r, 500);
            return;
        }
        // check if user with given email exists, return 409 if it exists
        if (resultHasNext) {
            this.sendStatus(r, 409);
            return;
        }
        ResultSet rs2;
        try {
			rs2 = this.dao.postUser(email, password, name);
            rs2.next();
            String uid = rs2.getString("uid");
            JSONObject resp = new JSONObject();
            resp.put("status", "OK");
            resp.put("uid", uid);
            this.sendResponse(r, resp, 200);
		}
		catch (SQLException e) {
            e.printStackTrace();
			this.sendStatus(r, 500);
			return;
		}
    }
}
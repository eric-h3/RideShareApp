package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.sql.*;

public class Login extends Endpoint {

    /**
     * POST /user/login
     * @body email, password
     * @return 200, 400, 401, 404, 500
     * Login a user into the system if the given information matches the 
     * information of the user in the database.
     */
    
    @Override
    public void handlePost(HttpExchange r) throws IOException, JSONException {
        // TODO
        String body = Utils.convert(r.getRequestBody());
		JSONObject deserialized = new JSONObject(body);
        String email = null;
		String password = null;
        if (!deserialized.has("email") || !deserialized.has("password")) {
            this.sendStatus(r, 400);
            return;
        }
        if (deserialized.get("email").getClass() != String.class) {
            this.sendStatus(r, 400);
            return;
        }
        email = deserialized.getString("email");
        if (deserialized.get("password").getClass() != String.class) {
            this.sendStatus(r, 400);
            return;
        }
        password = deserialized.getString("password");

        // make query to check if user with given email exists, return 500 if error
        ResultSet rs1;
        boolean resultHasNext;
        int uid;
        try {
            rs1 = this.dao.getUsersFromEmail(email);
            resultHasNext = rs1.next();
        } 
        catch (SQLException e) {
            e.printStackTrace();
            this.sendStatus(r, 500);
            return;
        }
        // check if user with given email exists, return 404 if not
        if (!resultHasNext) {
            this.sendStatus(r, 404);
            return;
        }

        try {
            uid = rs1.getInt("uid");
        }
        catch (SQLException e) {
            e.printStackTrace();
            this.sendStatus(r, 500);
            return;
        }

        // check if the password does not match, return 401 if so
        try {
            if (!rs1.getString("password").equals(password)) {
                this.sendStatus(r, 401);
                return;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
			this.sendStatus(r, 500);
			return;
		}

        JSONObject resp = new JSONObject();
        resp.put("status", "OK");
        resp.put("uid", uid);
        this.sendResponse(r, resp, 200);
    }
}

package ca.utoronto.utm.mcs;

import java.io.IOException;
import java.net.InetSocketAddress;
import com.sun.net.httpserver.HttpServer;

public class App {
    static int PORT = 8000;
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", PORT), 0);
        server.createContext("/location/user", new User());
        server.createContext("/location/", new Location());
        server.createContext("/location/road", new Road());
        server.createContext("/location/hasRoute", new Route());
        server.createContext("/location/route", new Route());
        
        // TODO: Add server contexts here. Do not set executors for the server, you shouldn't need them.
        server.createContext("/location/nearbyDriver/", new Nearby());
        server.createContext("/location/navigation/", new Navigation());
        server.start();
        System.out.printf("Server started on port %d...\n", PORT);
    }
}

package ca.utoronto.utm.mcs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import com.mongodb.client.FindIterable;

import org.bson.Document;
import org.json.JSONArray;

public class Utils {
    public static String convert(InputStream inputStream) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            return br.lines().collect(Collectors.joining(System.lineSeparator()));
        }
    }
    public static JSONArray findIterableToJSONArray(FindIterable<Document> docs) throws Exception {
        JSONArray arr = new JSONArray();
        int i = 0;
        for (Document doc : docs) {
            arr.put(i, doc.toJson());
            i++;
        }
        return arr;
    }
}

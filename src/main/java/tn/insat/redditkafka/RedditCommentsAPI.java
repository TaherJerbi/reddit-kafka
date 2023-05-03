package tn.insat.redditkafka;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RedditCommentsAPI {
    public static String request(String url_string, String method) throws IOException {
        URL url = new URL(url_string);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");
        connection.setDoOutput(true);

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        String jsonResponse = response.toString();
        // Handle the JSON response here as needed
        
        return jsonResponse;
    }

    public static RedditCommentsReponse getComments(int limit) throws IOException {
        String url_string = "https://www.reddit.com/r/chatgpt/comments/.json?limit=" + limit;

        String jsonResponse = request(url_string, "GET");

        Gson gson = new Gson();
        RedditCommentsReponse response = gson.fromJson(jsonResponse, RedditCommentsReponse.class);

        return response;
    }

    public static RedditCommentsReponse getComments(int limit, String subreddit) throws IOException {
        String url_string = "https://www.reddit.com/r/" + subreddit + "/comments/.json?limit=" + limit;

        String jsonResponse = request(url_string, "GET");

        Gson gson = new Gson();

        RedditCommentsReponse response = gson.fromJson(jsonResponse, RedditCommentsReponse.class);

        return response;
    }


}

package org.example;

import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class Zavd2 {

    private static final String BASE_URL = "https://jsonplaceholder.typicode.com";

    public static void main(String[] args) {
        try {
            int userId = 1;
            int postId = getLatestPostId(userId);

            if (postId > 0) {
                String commentsJson = getUserPostComments(postId);
                writeCommentsToFile(userId, postId, commentsJson);
                System.out.println("Коментарі успішно записані у файл.");
            } else {
                System.out.println("Не вдалося отримати id останнього поста для користувача.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Метод для отримання id останнього поста користувача
    private static int getLatestPostId(int userId) throws IOException {
        String apiUrl = BASE_URL + "/users/" + userId + "/posts";
        String response = sendGetRequest(apiUrl);
        return parseLatestPostId(response);
    }

    // Метод для отримання коментарів до останнього поста користувача
    private static String getUserPostComments(int postId) throws IOException {
        String apiUrl = BASE_URL + "/posts/" + postId + "/comments";
        return sendGetRequest(apiUrl);
    }

    // Метод для запису коментарів у файл
    private static void writeCommentsToFile(int userId, int postId, String commentsJson) throws IOException {
        String fileName = "user-" + userId + "-post-" + postId + "-comments.json";
        try (FileWriter fileWriter = new FileWriter(fileName)) {
            fileWriter.write(commentsJson);
        }
    }

    // Метод для відправки HTTP GET-запиту
    static String sendGetRequest(String apiUrl) throws IOException {
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (Scanner scanner = new Scanner(connection.getInputStream())) {
                StringBuilder response = new StringBuilder();
                while (scanner.hasNextLine()) {
                    response.append(scanner.nextLine());
                }
                return response.toString();
            }
        } else {
            throw new IOException("Failed to get response from the server. Response Code: " + responseCode);
        }
    }

    // Метод для парсингу JSON і отримання id останнього поста
    private static int parseLatestPostId(String json) {
        int idIndex = json.lastIndexOf("\"id\"");
        if (idIndex != -1) {
            int colonIndex = json.indexOf(":", idIndex);
            int commaIndex = json.indexOf(",", idIndex);
            if (colonIndex != -1 && commaIndex != -1) {
                String idString = json.substring(colonIndex + 1, commaIndex).trim();
                return Integer.parseInt(idString);
            }
        }
        return 0;
    }
}

package org.example;

import org.jsoup.Jsoup;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import static org.example.Zavd2.sendGetRequest;

public class Main {
    private static final String API_URL = "https://jsonplaceholder.typicode.com/users";
    public Main() throws IOException {
    }

    public static void main(String[] args) throws IOException {
//        // Створення користувача
//        try {
//            String newUserJson = "{\"name\":\"John Doe\",\"username\":\"johndoe\",\"email\":\"john.doe@example.com\"}";
//            String createdUserJson = createUser(newUserJson);
//
//            System.out.println("Створений користувач:\n" + createdUserJson);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        // Оновлення користувача
//        try {
//            String updatedUserJson = updateExistingUser("{\"id\": 1, \"name\":\"John Doe\",\"username\":\"johndoe\",\"email\":\"john.doe@example.com\"}");
//
//            System.out.println("Оновлений користувач:\n" + updatedUserJson);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        // Видалення користувача
        try {
            int userId = 1;
            deleteExistingUser(userId);
            System.out.println("Користувач успішно видалений.");
        } catch (IOException e) {
            e.printStackTrace();
        }
//
//        // Отримання інформації про всіх користувачів
//        getAllUsers();
//
//        // Отримання інформації про користувача за id
//        getUserById(2);
//
//        // Отримання інформації про користувача за username
//        getUserByUsername("Bret");
//
//        // 3 завдання
//        printOpenTasks(1);

    }

    private static String createUser(String newUserJson) throws IOException {
        URL url = new URL(API_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // Налаштовуємо параметри HTTP-запиту
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        // Відправляємо дані на сервер
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = newUserJson.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        // Зчитуємо відповідь сервера
        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            return response.toString();
        } finally {
            connection.disconnect();
        }
    }

    private static String updateExistingUser(String updatedUserJson) throws Exception {
        URL url = new URL(API_URL + "/1");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("PUT");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = updatedUserJson.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            return response.toString();
        } finally {
            connection.disconnect();
        }
    }

    private static void deleteExistingUser(int userId) throws IOException {
        String apiUrl = API_URL + "/"+ userId;
        URL url = new URL(apiUrl);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("DELETE");

        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new IOException("Failed to delete user. Response Code: " + responseCode);
        }
    }

    private static void getAllUsers() throws IOException {
        String text = Jsoup.connect(API_URL)
                .ignoreContentType(true)
                .get()
                .body()
                .text();

        System.out.println("Інформація про користувачів:\n" + text);
    }

    private static void getUserById(int id) throws IOException {
        String text = Jsoup.connect("https://jsonplaceholder.typicode.com/users/" + id)
                .ignoreContentType(true)
                .get()
                .body()
                .text();
        System.out.println("Користувач з id " + id + " це: " + text);
    }

    private static void getUserByUsername(String username) throws IOException {
        String text = Jsoup.connect("https://jsonplaceholder.typicode.com/users?username=" + username)
                .ignoreContentType(true)
                .get()
                .body()
                .text();
        System.out.println("Користувач з username " + username + " це: " + text);
    }

    private static void printOpenTasks(int Id) throws IOException {
        String apiUrl = "https://jsonplaceholder.typicode.com/users/" + Id + "/todos";
        String todosJson = sendGetRequest(apiUrl);
        System.out.println("Відкриті задачі для користувача з ідентифікатором " + Id + ":");
        parseAndPrintOpenTasks(todosJson);
    }
    private static void parseAndPrintOpenTasks(String json) {
        JsonArray tasksArray = JsonParser.parseString(json).getAsJsonArray();

        System.out.println("Відкриті задачі:");

        for (int i = 0; i < tasksArray.size(); i++) {
            JsonObject task = tasksArray.get(i).getAsJsonObject();
            boolean completed = task.getAsJsonPrimitive("completed").getAsBoolean();

            if (!completed) {
                int taskId = task.getAsJsonPrimitive("id").getAsInt();
                String title = task.getAsJsonPrimitive("title").getAsString();
                System.out.println("Task ID: " + taskId + ", Title: " + title);
            }
        }

    }
}
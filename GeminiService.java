package com.dcdoctor.service;

import com.google.gson.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class GeminiService {

    private static final String API_KEY = "AIzaSyDdpSUNuj57q8cn4Mz0rbex5uocJjSaEoE";
    private static final String URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-3-flash-preview:generateContent?key=" + API_KEY;

    // ✅ cache để tránh gọi lại
    private static final Map<String, String> cache = new HashMap<>();

    public static String askGemini(String userPrompt) {

        // 🔥 1. kiểm tra cache trước
        if (cache.containsKey(userPrompt)) {
            return cache.get(userPrompt);
        }

        int maxRetry = 3; // thử lại tối đa 3 lần

        for (int attempt = 0; attempt < maxRetry; attempt++) {
            try {
                JsonObject textPart = new JsonObject();
                textPart.addProperty("text", userPrompt);

                JsonArray partsArray = new JsonArray();
                partsArray.add(textPart);

                JsonObject contentsPart = new JsonObject();
                contentsPart.add("parts", partsArray);

                JsonArray contentsArray = new JsonArray();
                contentsArray.add(contentsPart);

                JsonObject finalBody = new JsonObject();
                finalBody.add("contents", contentsArray);

                HttpClient client = HttpClient.newHttpClient();

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(URL))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(finalBody.toString(), StandardCharsets.UTF_8))
                        .build();

                HttpResponse<String> response =
                        client.send(request, HttpResponse.BodyHandlers.ofString());

                // ✅ SUCCESS
                if (response.statusCode() == 200) {
                    JsonObject jsonObject = JsonParser.parseString(response.body()).getAsJsonObject();

                    String result = jsonObject.getAsJsonArray("candidates")
                            .get(0).getAsJsonObject()
                            .getAsJsonObject("content")
                            .getAsJsonArray("parts")
                            .get(0).getAsJsonObject()
                            .get("text").getAsString();

                    // lưu cache
                    cache.put(userPrompt, result);

                    return result;
                }

                // 🔥 429 → đợi rồi retry
                else if (response.statusCode() == 429) {
                    try {
                        Thread.sleep(3000); // đợi 3 giây
                    } catch (InterruptedException ignored) {}
                }

                // ❗ lỗi khác
                else {
                    return "Lỗi API: " + response.statusCode();
                }

            } catch (Exception e) {
                return "Lỗi kết nối mạng.";
            }
        }

        // nếu retry vẫn fail
        return "⚠️ AI đang quá tải, thử lại sau 10–20 giây.";
    }
    public String ask(String userPrompt) {
        return askGemini(userPrompt);
    }
}
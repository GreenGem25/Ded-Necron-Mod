package com.ded.necronmod.anekdot;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JokeParser {

    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    private static final Pattern JSON_PATTERN = Pattern.compile("JSON\\.parse\\('(.*?)'\\);", Pattern.DOTALL);
    private static final Random RANDOM = new Random();
    private static final Gson GSON = new Gson();

    public static CompletableFuture<String> fetchAnekdotAsync() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://www.anekdot.ru/rss/randomu.html"))
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                .GET()
                .build();

        return CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8))
                .thenApply(response -> {
                    if (response.statusCode() == 200) {
                        try {
                            String body = response.body();
                            Matcher matcher = JSON_PATTERN.matcher(body);

                            if (matcher.find()) {
                                String rawJson = matcher.group(1);

                                // 1. Исправляем экранирование обратно в нормальный JSON
                                // Заменяем экранированные кавычки \" на "
                                String cleanJson = rawJson.replace("\\\"", "\"");

                                // 2. Заменяем двойные обратные слэши \\ на один \
                                cleanJson = cleanJson.replace("\\\\", "\\");

                                // 3. Убираем символы переноса строк и табуляции внутри самого JSON (если есть)
                                cleanJson = cleanJson.replace("\r", "").replace("\n", " ");

                                // Парсим полученный JSON-массив
                                List<String> jokes = GSON.fromJson(cleanJson, new TypeToken<List<String>>(){}.getType());

                                if (jokes != null && !jokes.isEmpty()) {
                                    String randomJoke = jokes.get(RANDOM.nextInt(jokes.size()));
                                    return cleanJokeText(randomJoke);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            return "Ошибка при обработке анекдота.";
                        }
                    }
                    return "Не удалось получить анекдот (Код: " + response.statusCode() + ")";
                })
                .exceptionally(ex -> "Ошибка сети: " + ex.getMessage());
    }

    private static String cleanJokeText(String rawText) {
        return rawText
                .replaceAll("(?i)<br\\s*/?>", "\n") // HTML <br> -> Перенос строки
                .replaceAll("<[^>]*>", "")          // Удаляем остаточные HTML теги
                .replaceAll("©.*$", "")             // Убираем подпись автора
                .trim();
    }
}
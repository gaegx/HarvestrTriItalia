package com.gaegxh.harvester.config;

import jakarta.annotation.PostConstruct;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.stream.Collectors;



public class CookieConfig {

    private final Map<String, String> cookieStore = new ConcurrentHashMap<>();


    public void init() {
        refreshCookies();
    }

    public synchronized void refreshCookies() {
        try {

            HttpResponse<String> response = Unirest.get("https://www.lefrecce.it/Channels.Website.WEB/")
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36")
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                    .header("Accept-Language", "en-US,en;q=0.5")
                    .asString();

            updateSessionCookies(response);

        } catch (Exception e) {
            System.err.println("Error refreshing cookies: " + e.getMessage());
            cookieStore.clear();
        }
    }

    public synchronized void updateSessionCookies(HttpResponse<?> response) {
        List<String> setCookieHeaders = response.getHeaders().get("Set-Cookie");
        if (setCookieHeaders != null) {
            for (String cookieHeader : setCookieHeaders) {
                String[] parts = cookieHeader.split(";", 2);
                String keyValue = parts[0].trim();
                int equalsIndex = keyValue.indexOf('=');
                if (equalsIndex > 0) {
                    String key = keyValue.substring(0, equalsIndex).trim();
                    String value = keyValue.substring(equalsIndex + 1).trim();

                    if (value.isEmpty()) {
                        cookieStore.remove(key);
                    } else {
                        cookieStore.put(key, value);
                    }
                }
            }
        }
    }

    public synchronized String getCookiesHeader() {
        return cookieStore.entrySet().stream()
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining("; "));
    }
}

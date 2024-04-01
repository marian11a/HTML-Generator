package com.example.HTMLGenerator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Generator {
    static String API_URL = "https://api-inference.huggingface.co/models/mistralai/Mistral-7B-Instruct-v0.2";

    //Go to https://huggingface.co/settings/tokens and get your API token!
    static String API_TOKEN = "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";

    public String generate(String prompt) {
        try {

            prompt = prompt.substring(1, prompt.length() - 1);
            int maxNewTokens = 5000; // Adjust the number of tokens to generate more or less text
            double temperature = 0.7; // Adjust the temperature to control randomness in generation

            String generatedText = generateText(prompt, maxNewTokens, temperature);
            while (generatedText.contains("\\n")) {
                generatedText = generatedText.replace("\\n", "\n");
            }
            generatedText = extractHtml(generatedText);

            while (generatedText.contains("\\")) {
                generatedText = generatedText.replace("\\", "");
            }

            return generatedText;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String generateText(String prompt, int maxNewTokens, double temperature) throws IOException {
        URL url = new URL(API_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", "Bearer " + API_TOKEN);
        conn.setDoOutput(true);

        String payload = "{\"inputs\": \"" + prompt + "\", \"parameters\": {\"max_new_tokens\": " + maxNewTokens +
                ", \"temperature\": " + temperature + "}}";

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = payload.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            return response.toString();
        } finally {
            conn.disconnect();
        }
    }

    private String extractHtml(String text) {
        String regex = "(?<=<!DOCTYPE html>).*?(?=</html>)";
        Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            return "<!DOCTYPE html>" + matcher.group(0) + "</html>";
        } else {
            return "";
        }
    }
}

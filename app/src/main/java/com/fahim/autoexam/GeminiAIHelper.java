package com.fahim.autoexam;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class GeminiAIHelper {
    private static final String TAG = "GeminiAIHelper";

    private static final String API_KEY = BuildConfig.GEMINI_API_KEY;

    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + API_KEY;

    public interface OnQuestionGeneratedListener {
        void onSuccess(String generatedQuestions);
        void onError(String errorMessage);
        void onProgress(String message);
    }

    public void generateQuestions(
            QuestionPaperData paperData,
            String syllabusText,
            OnQuestionGeneratedListener listener
    ) {
        final String finalSyllabusText;

        if (syllabusText != null && syllabusText.length() > 15000) {
            finalSyllabusText = syllabusText.substring(0, 15000) + "\n... (syllabus truncated)";
        } else {
            finalSyllabusText = syllabusText;
        }

        new Thread(() -> {
            try {
                notifyProgress(listener, "Connecting to AI...");

                String prompt = buildPrompt(paperData, finalSyllabusText);
                Log.d(TAG, "Prompt created, length: " + prompt.length());

                notifyProgress(listener, "Analyzing syllabus and pattern...");

                JSONObject requestBody = new JSONObject();
                JSONArray contents = new JSONArray();
                JSONObject content = new JSONObject();
                JSONArray parts = new JSONArray();
                JSONObject part = new JSONObject();

                part.put("text", prompt);
                parts.put(part);
                content.put("parts", parts);
                contents.put(content);
                requestBody.put("contents", contents);

                JSONObject generationConfig = new JSONObject();
                generationConfig.put("temperature", 0.7);
                generationConfig.put("maxOutputTokens", 8192);
                requestBody.put("generationConfig", generationConfig);

                String jsonRequest = requestBody.toString();
                Log.d(TAG, "Request body length: " + jsonRequest.length());

                URL url = new URL(API_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);
                connection.setConnectTimeout(60000);
                connection.setReadTimeout(60000);

                notifyProgress(listener, "Generating questions...");
                OutputStream os = connection.getOutputStream();
                os.write(jsonRequest.getBytes(StandardCharsets.UTF_8));
                os.flush();
                os.close();

                int responseCode = connection.getResponseCode();
                Log.d(TAG, "Response Code: " + responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    String generatedText = parseResponse(response.toString());

                    if (generatedText != null && !generatedText.isEmpty()) {
                        notifySuccess(listener, generatedText);
                    } else {
                        notifyError(listener, "Empty response from API");
                    }

                } else {
                    BufferedReader errorReader = new BufferedReader(
                            new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8));
                    StringBuilder errorResponse = new StringBuilder();
                    String errorLine;
                    while ((errorLine = errorReader.readLine()) != null) {
                        errorResponse.append(errorLine);
                    }
                    errorReader.close();

                    String errorMsg = "API Error " + responseCode + ": " + errorResponse.toString();
                    Log.e(TAG, errorMsg);
                    notifyError(listener, errorMsg);
                }

                connection.disconnect();

            } catch (Exception e) {
                Log.e(TAG, "Exception in generateQuestions", e);
                notifyError(listener, "Network Error: " + e.getMessage());
            }
        }).start();
    }

    private String buildPrompt(QuestionPaperData paperData, String syllabusText) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("You are an expert university exam paper creator.\n\n");

        // STEP 1 — Pattern for structure only
        prompt.append("STEP 1 — ANALYZE THE PATTERN (structure only, do not copy questions):\n");
        prompt.append("Read the following previous year question paper carefully.\n");
        prompt.append("Extract ONLY the structure — number of questions, sections, marks per question,\n");
        prompt.append("attempt instructions, question types.\n");
        prompt.append("Do NOT copy or reuse any question from this paper.\n\n");

        String patternText = paperData.getPatternPdfContent();
        if (patternText != null && patternText.length() > 15000) {
            patternText = patternText.substring(0, 15000) + "\n... (pattern paper truncated)";
        }
        prompt.append("[PATTERN PAPER START]\n");
        prompt.append(patternText != null ? patternText : "(not provided)").append("\n");
        prompt.append("[PATTERN PAPER END]\n\n");

        // STEP 2 — Syllabus as content source
        prompt.append("STEP 2 — YOUR CONTENT SOURCE (use only this to write new questions):\n");
        prompt.append("The following is the syllabus. All new questions must come from this content only.\n");
        prompt.append("Do NOT use anything from the pattern paper as question content.\n\n");

        prompt.append("[SYLLABUS START]\n");
        prompt.append(syllabusText).append("\n");
        prompt.append("[SYLLABUS END]\n\n");

        // STEP 3 — Generation instructions
        prompt.append("STEP 3 — GENERATE THE PAPER:\n");
        prompt.append("Now generate a complete brand new question paper following these strict rules:\n");

        prompt.append("1. Print this header exactly at the top:\n");
        prompt.append("   Class: ").append(paperData.getClassName()).append("                           ");
        prompt.append("   Subject: ").append(paperData.getSubjectName()).append("                           ");
        prompt.append("   Date: ").append(paperData.getDate() != null ? paperData.getDate() : "________").append("\n");
        prompt.append("   Duration: ").append(paperData.getDuration()).append("\n\n");

        prompt.append("2. Follow the EXACT same structure as the pattern paper —\n");
        prompt.append("   same number of questions, same sections, same marks per question, same total marks but this total marks should display on the header you can write it after duration on *right side* of the paper \n\n\n");
        prompt.append("   exact same notes or instructions points before actual questions )\n\n");
        prompt.append("   same attempt instructions (e.g. attempt any three)\n\n");

        // --- ADDED NUMBERING LOGIC HERE ---
        prompt.append("3. STRICT NUMBERING AND LABELING RULES:\n");
        prompt.append("   - Every main question MUST start with 'Q.' followed by the number (e.g., Q.1, Q.2, Q.3).\n");
        prompt.append("   - Every sub-question MUST start with a lowercase letter followed by a period (e.g., a., b., c., d., e., f.).\n");
        prompt.append("   - Do NOT use bullet points (- or *) or Roman numerals unless the pattern paper specifically uses them.\n");
        prompt.append("   - Ensure there is a newline between each sub-question.\n\n");

        prompt.append("4. Every single question must be NEW and taken only from the syllabus content\n\n");

        prompt.append("5. Formatting: Plain text only. Use only 'Q.1' and 'a.' for structure. Do NOT use markdown symbols like **, #, or __.\n\n");

        prompt.append("6. Example Format to follow:\n");
        prompt.append("   Q.1 Attempt any three of the following: 15\n");
        prompt.append("   a. [Your New Question 1]\n");
        prompt.append("   b. [Your New Question 2]\n");
        prompt.append("   ...and so on.\n\n");

        prompt.append("7. Do not include any answers or answer keys and please now don't write page number\n");

        return prompt.toString();
    }

    private String parseResponse(String responseBody) throws Exception {
        try {
            JSONObject jsonResponse = new JSONObject(responseBody);

            if (!jsonResponse.has("candidates")) {
                Log.e(TAG, "No candidates in response: " + responseBody);
                return "Error: Invalid API response";
            }

            JSONArray candidates = jsonResponse.getJSONArray("candidates");
            if (candidates.length() == 0) {
                return "Error: Empty candidates array";
            }

            JSONObject firstCandidate = candidates.getJSONObject(0);
            JSONObject content = firstCandidate.getJSONObject("content");
            JSONArray parts = content.getJSONArray("parts");
            JSONObject firstPart = parts.getJSONObject(0);
            String text = firstPart.getString("text");

            return text.trim();

        } catch (Exception e) {
            Log.e(TAG, "Error parsing response", e);
            throw new Exception("Failed to parse API response: " + e.getMessage());
        }
    }

    private void notifySuccess(OnQuestionGeneratedListener listener, String result) {
        new Handler(Looper.getMainLooper()).post(() -> listener.onSuccess(result));
    }

    private void notifyError(OnQuestionGeneratedListener listener, String error) {
        new Handler(Looper.getMainLooper()).post(() -> listener.onError(error));
    }

    private void notifyProgress(OnQuestionGeneratedListener listener, String message) {
        new Handler(Looper.getMainLooper()).post(() -> listener.onProgress(message));
    }
}

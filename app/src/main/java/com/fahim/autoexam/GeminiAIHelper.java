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
        // Create final variable for lambda
        final String finalSyllabusText;

        // Truncate syllabus if too long (Gemini has token limits)
        if (syllabusText != null && syllabusText.length() > 15000) {
            finalSyllabusText = syllabusText.substring(0, 15000) + "\n... (syllabus truncated)";
        } else {
            finalSyllabusText = syllabusText;
        }

        new Thread(() -> {
            try {
                // Notify progress
                notifyProgress(listener, "Connecting to AI...");

                String prompt = buildPrompt(paperData, finalSyllabusText);
                Log.d(TAG, "Prompt created, length: " + prompt.length());

                notifyProgress(listener, "Analyzing syllabus...");

                // Create request body
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

                // Optional: Add generation config for better control
                JSONObject generationConfig = new JSONObject();
                generationConfig.put("temperature", 0.7);
                generationConfig.put("maxOutputTokens", 2048);
                requestBody.put("generationConfig", generationConfig);

                String jsonRequest = requestBody.toString();
                Log.d(TAG, "Request body: " + jsonRequest);

                // Make API call
                URL url = new URL(API_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);
                connection.setConnectTimeout(30000); // 30 seconds timeout
                connection.setReadTimeout(30000);

                // Send request
                notifyProgress(listener, "Generating questions...");
                OutputStream os = connection.getOutputStream();
                os.write(jsonRequest.getBytes(StandardCharsets.UTF_8));
                os.flush();
                os.close();

                // Get response code
                int responseCode = connection.getResponseCode();
                Log.d(TAG, "Response Code: " + responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Read success response
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    Log.d(TAG, "Response: " + response.toString());

                    String generatedText = parseResponse(response.toString());

                    if (generatedText != null && !generatedText.isEmpty()) {
                        notifySuccess(listener, generatedText);
                    } else {
                        notifyError(listener, "Empty response from API");
                    }

                } else {
                    // Read error response
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
        int numQuestions = 10;
        try {
            numQuestions = Integer.parseInt(paperData.getNoOfQuestionsPerUnit());
        } catch (NumberFormatException ignored) {}

        int totalMarks = 50;
        try {
            totalMarks = Integer.parseInt(paperData.getMarks());
        } catch (NumberFormatException ignored) {}

        StringBuilder prompt = new StringBuilder();

        prompt.append("You are an expert university examination question paper creator.\n\n");

        // Paper header info - AI should reproduce this at the top
        prompt.append("**PAPER HEADER (print this exactly at the top of the output):**\n");
//        prompt.append(paperData.getCollegeName()).append("\n");
//        prompt.append("Exam: ").append(paperData.getQuestionPaperName()).append("\n");
        prompt.append("Class: ").append(paperData.getClassName()).append("\n");
        prompt.append("Subject: ").append(paperData.getSubjectName()).append("\n");
        prompt.append("Date: ").append(paperData.getDate() != null ? paperData.getDate() : "________").append("\n");
        prompt.append("Total Marks: ").append(totalMarks).append("\n");
        prompt.append("Duration: ").append(paperData.getDuration()).append("\n");
        prompt.append("\n\n");

        // Syllabus content
        prompt.append("**SYLLABUS CONTENT (use this to frame questions):**\n");
        prompt.append(syllabusText).append("\n\n");

        // Question type specific instructions
        String questionType = paperData.getQuestionType();

        if ("MCQ".equalsIgnoreCase(questionType)) {
            prompt.append(getMCQInstructions(numQuestions, totalMarks));
        } else if ("Short Answer".equalsIgnoreCase(questionType)) {
            prompt.append(getShortAnswerInstructions(numQuestions, totalMarks));
        } else if ("Long Answer".equalsIgnoreCase(questionType)) {
            prompt.append(getLongAnswerInstructions(numQuestions, totalMarks));
        } else {
            prompt.append(getMCQInstructions(numQuestions, totalMarks));
        }

        return prompt.toString();
    }

    private String getMCQInstructions(int numQuestions, int totalMarks) {
        int marksPerQuestion = numQuestions > 0 ? totalMarks / numQuestions : 1;

        return "**TASK:**\n" +
                "Generate exactly " + numQuestions + " Multiple Choice Questions.\n\n" +
                "**OUTPUT FORMAT (strictly follow, plain text only, no markdown):**\n" +
                "Print the paper header first, then a blank line, then:\n\n" +
                "Q1. [Question text]\n" +
                "    A) [Option A]\n" +
                "    B) [Option B]\n" +
                "    C) [Option C]\n" +
                "    D) [Option D]\n\n" +
                "Q2. [Next question]...\n\n" +
                "**RULES:**\n" +
                "- Total questions: " + numQuestions + "\n" +
                "- Each MCQ carries " + marksPerQuestion + " mark(s)\n" +
                "- Cover different topics evenly from the syllabus\n" +
                "- Mix difficulty: 30% easy, 50% medium, 20% hard\n" +
                "- All four options must be plausible\n" +
                "- Do NOT include answers or answer keys\n" +
                "- Do NOT use any markdown formatting (no **, no #, no ```)\n" +
                "- Output plain text only\n\n" +
                "Generate the complete question paper now:";
    }

    private String getShortAnswerInstructions(int numQuestions, int totalMarks) {
        int marksPerQuestion = numQuestions > 0 ? totalMarks / (numQuestions/2) : 2;

        return "**TASK:**\n" +
                "Generate exactly " + numQuestions + " Short Answer Questions.\n\n" +
                "**OUTPUT FORMAT (strictly follow, plain text only, no markdown):**\n" +
                "Print the paper header first, then a blank line, then:\n\n" +
                "Q1. [Question text] (" + marksPerQuestion + " marks)\n\n" +
                "Q2. [Next question]...\n\n" +
                "**RULES:**\n" +
                "- Total questions: " + numQuestions + "\n" +
                "- Each question carries " + marksPerQuestion + " marks\n" +
                "- Questions should be answerable in 50-100 words\n" +
                "- Use action verbs: Define, Explain, List, Describe, Differentiate, State\n" +
                "- Cover different topics evenly from the syllabus\n" +
                "- Do NOT include answers\n" +
                "- Do NOT use any markdown formatting (no **, no #, no ```)\n" +
                "- Output plain text only\n\n" +
                "Generate the complete question paper now:";
    }

    private String getLongAnswerInstructions(int numQuestions, int totalMarks) {
        int marksPerQuestion = numQuestions > 0 ? totalMarks / (numQuestions/2) : 5;

        return "**TASK:**\n" +
                "Generate exactly " + numQuestions + " Long Answer Questions.\n\n" +
                "**OUTPUT FORMAT (strictly follow, plain text only, no markdown):**\n" +
                "Print the paper header first, then a blank line, then:\n\n" +
                "Q1. [Question text] (" + marksPerQuestion + " marks)\n\n" +
                "Q2. [Next question]...\n\n" +
                "**RULES:**\n" +
                "- Total questions: " + numQuestions + "\n" +
                "- Each question carries " + marksPerQuestion + " marks\n" +
                "- Questions should require 200-300 word answers\n" +
                "- Use analytical verbs: Analyze, Evaluate, Discuss, Compare, Justify, Elaborate\n" +
                "- Cover major topics from the syllabus\n" +
                "- May include sub-parts like (a), (b) for higher mark questions\n" +
                "- Do NOT include answers\n" +
                "- Do NOT use any markdown formatting (no **, no #, no ```)\n" +
                "- Output plain text only\n\n" +
                "Generate the complete question paper now:";
    }

    private String parseResponse(String responseBody) throws Exception {
        try {
            JSONObject jsonResponse = new JSONObject(responseBody);

            // Check if there are candidates
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
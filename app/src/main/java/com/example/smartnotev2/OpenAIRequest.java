package com.example.smartnotev2;

import java.util.List;

public class OpenAIRequest {
    String model;
    List<Message> messages;

    public OpenAIRequest(String text) {
        this.model = "llama-3.1-8b-instant";

        this.messages = List.of(
                new Message("system", "You summarize text and generate a short title."),
                new Message("user", "Give a short title and summary for this text. Respond exactly like this:\nTitle: ...\nSummary: ...\n\n" + text)
        );
    }

    static class Message {
        String role;
        String content;

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }
}
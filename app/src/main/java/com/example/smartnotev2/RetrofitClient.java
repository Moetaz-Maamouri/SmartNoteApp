package com.example.smartnotev2;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static final String BASE_URL = "https://api.groq.com/openai/";
    private static OpenAIApi instance;

    public static OpenAIApi getClient() {
        if (instance == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            instance = retrofit.create(OpenAIApi.class);
        }
        return instance;
    }
}
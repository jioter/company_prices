package com.parse.config;

import java.net.http.HttpClient;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfigurations {
    public static final ExecutorService executor = Executors.newFixedThreadPool(100);

    @Bean
    public HttpClient httpClient() {
        return HttpClient.newHttpClient();
    }

    @Bean
    public CompletionService<String> executorService() {
        return new ExecutorCompletionService<>(executor);
    }

}

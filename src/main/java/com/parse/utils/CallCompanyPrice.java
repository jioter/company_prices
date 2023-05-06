package com.parse.utils;

import com.parse.config.AppConfigurations;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.concurrent.Callable;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.annotation.PropertySource;

@PropertySource("classpath:application.yaml")
@RequiredArgsConstructor
public class CallCompanyPrice implements Callable<String> {

    private final String eixApiURL;

    private final String symbol;

    private final String token;

    private final AppConfigurations appConfigurations;


    @SneakyThrows
    @Override
    public String call() {
        URL url = new URL(eixApiURL + "/stock/" + symbol + "/quote?token=" + token);
        final var httpClient = appConfigurations.httpClient();

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url.toString()))
            .GET()
            .build();

        return httpClient.sendAsync(request, BodyHandlers.ofString())
            .thenApplyAsync(HttpResponse::body)
            .join();
    }

}

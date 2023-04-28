package com.parse.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;

@AllArgsConstructor
public class CallCompanyPrice implements Callable<String> {

    @Value("${iexapi.token}")
    private String TOKEN;

    @Value("${iexapi.url}")
    private String URL;

    private final String symbol;

    public CallCompanyPrice(String symbol) {
        this.symbol = symbol;
    }


    @SneakyThrows
    @Override
    public String call() {
        URL url = new URL(URL + "/stock/" + symbol + "/quote/latestPrice?token=" + TOKEN);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(
            new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        return content.toString();
    }
}

package com.parse.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;
import lombok.SneakyThrows;

public class CallCompanyPrice implements Callable<String> {
    final String token = "pk_08ec385f33dd4525b67ebf1efc7ff89c";
    private final String symbol;

    public CallCompanyPrice(String symbol){
        this.symbol = symbol;
    }


    @SneakyThrows
    @Override
    public String call() {
        URL url = new URL("https://cloud.iexapis.com/"
            + "stable/stock/" + symbol + "/quote/latestPrice?token=" + token);
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

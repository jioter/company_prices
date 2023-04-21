package com.parse.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.parse.entities.Company;
import com.parse.repository.CompanyRepository;
import com.parse.utils.CallCompanyPrice;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ParseDataService {

    private final CompanyRepository companyRepository;

    final String token = "pk_08ec385f33dd4525b67ebf1efc7ff89c";

    @SneakyThrows
    public StringBuffer getCompaniesData() {
        URL url = new URL("https://cloud.iexapis.com/"
            + "stable/ref-data/iex/symbols?token=" + token);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(
            new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        return content;
    }

    @SneakyThrows
    public void processCompanies(StringBuffer symbolsObjects) {
        final ObjectMapper objectMapper = new ObjectMapper();
        Company[] arrayOfCompanies = objectMapper.readValue(symbolsObjects.toString(),
            Company[].class);
        List<Company> listOfCompanies = Arrays.asList(arrayOfCompanies);
        companyRepository.saveAll(listOfCompanies);
        companyRepository.findAll();
    }

    public List<String> getCompaniesSymbolsFromDB() {
        return companyRepository.findAllCompanySymbols();
    }

    @SneakyThrows
    public List<String> getCompanyPrices() {
        final var companiesSymbols = getCompaniesSymbolsFromDB();

        ExecutorService executor = Executors.newFixedThreadPool(100);
        CompletionService<String> completionService =
            new ExecutorCompletionService<>(executor);

        for (String symbol : companiesSymbols) {
            completionService.submit(new CallCompanyPrice(symbol));
        }

        int received = 0;
        int errors = 0;
        List<String> result = new ArrayList<>();

        long startProcessingTime = System.currentTimeMillis();

        while (received < companiesSymbols.size()) {
            Future<String> resultFuture = completionService.take();
            try {
                String response = resultFuture.get();
                result.add(response);
                received++;
                System.out.println(response);
                System.out.println("Number of errors:" + errors);
                System.out.println("Response count:" + received);
                System.out.println("General requests sent:" + companiesSymbols.size());
                long elapsedTime
                    = System.currentTimeMillis() - startProcessingTime;
                System.out.println("Elapsed time:" + elapsedTime / 1000);
            } catch (Exception e) {
                errors++;
            }
        }
        executor.shutdown();
        return result;
    }

}

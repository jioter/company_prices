package com.parse.services;

import static com.parse.config.AppConfigurations.executor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.parse.config.AppConfigurations;
import com.parse.dto.CompanyDTO;
import com.parse.entities.CompanyEntity;
import com.parse.repository.CompanyRepository;
import com.parse.utils.CallCompanyPrice;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

@Service
@PropertySource("classpath:application.yaml")
public class ParseDataService {

    @Autowired
    private CompanyRepository companyRepository;

    @Value("${iexapi.token}")
    private String token;

    @Value("${iexapi.url}")
    private String eixApiURL;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    AppConfigurations appConfigurations;

    @SneakyThrows
    public CompanyDTO[] getCompaniesData() {
        var url = new URL(eixApiURL + "/ref-data/iex/symbols?token=" + token);
        final var httpClient = appConfigurations.httpClient();

        var request = HttpRequest.newBuilder()
            .uri(URI.create(url.toString()))
            .GET()
            .build();

        final var response = httpClient.sendAsync(request, BodyHandlers.ofString())
            .thenApplyAsync(HttpResponse::body)
            .join();

        return objectMapper.readValue(response, CompanyDTO[].class);
    }

    @SneakyThrows
    public void saveAllCompanies(CompanyDTO[] companyDTOs) {
        var entityList = mapCompanyDTOListToEntityList(companyDTOs);
        companyRepository.saveAll(entityList);
    }

    public List<String> getAllCompanySymbols() {
        return companyRepository.findAllCompanySymbols();
    }

    @SneakyThrows
    public List<String> getCompanyPrices() {
        final var companiesSymbols = getAllCompanySymbols();

        var completionService = appConfigurations.executorService();

        for (String symbol : companiesSymbols) {
            completionService.submit(
                new CallCompanyPrice(eixApiURL, symbol, token, appConfigurations));
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

    private List<CompanyEntity> mapCompanyDTOListToEntityList(CompanyDTO[] companyDTOs)
        throws JsonProcessingException {
        TypeReference<List<CompanyEntity>> entityListTypeRef = new TypeReference<>() {
        };

        var dtoListJson = objectMapper.writeValueAsString(companyDTOs);

        return objectMapper.readValue(dtoListJson, entityListTypeRef);
    }

}

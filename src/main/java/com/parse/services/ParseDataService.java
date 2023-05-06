package com.parse.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.parse.config.AppConfigurations;
import com.parse.dto.CompanyDTO;
import com.parse.dto.PriceDTO;
import com.parse.entities.CompanyEntity;
import com.parse.entities.PriceEntity;
import com.parse.repository.CompanyRepository;
import com.parse.repository.PriceRepository;
import com.parse.utils.CallCompanyPrice;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;
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

    @Autowired
    private PriceRepository priceRepository;

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
    public void saveAllCompanyData(CompanyDTO[] companyDTOs) {
        var entityList = mapCompanyDTOtoEntity(companyDTOs);
        final var filteredByActive = entityList.stream()
            .filter(el -> Boolean.parseBoolean(el.getIsEnabled()))
            .collect(Collectors.toList());
        companyRepository.saveAll(filteredByActive);
    }

    public List<String> getAllCompanySymbols() {
        return companyRepository.findAllCompanySymbols();
    }

    @SneakyThrows
    public List<PriceDTO> getCompanyPrices() {
        final var companiesSymbols = getAllCompanySymbols();

        var forkJoinPool = new ForkJoinPool();

        List<CompletableFuture<String>> futures = new ArrayList<>();

        for (String symbol : companiesSymbols) {
            futures.add(CompletableFuture.supplyAsync(
                () -> new CallCompanyPrice(eixApiURL, symbol, token, appConfigurations).call(),
                forkJoinPool));
        }

        List<PriceDTO> result = new ArrayList<>();
        long startProcessingTime = System.currentTimeMillis();
        int errors = 0;
        for (CompletableFuture<String> future : futures) {
            try {
                String response = future.get();
                if (!response.contains("429") && !response.contains("Too many requests")
                    && !response.contains("Unknown symbol")) {
                    final var priceDTO = objectMapper.readValue(response, PriceDTO.class);
                    result.add(priceDTO);
                }
            } catch (InterruptedException | ExecutionException e) {
                errors++;
            }
        }
        System.out.println("Number of errors: " + errors);
        System.out.println("General requests sent: " + companiesSymbols.size());
        long elapsedTime = System.currentTimeMillis() - startProcessingTime;
        System.out.println("Elapsed time: " + elapsedTime / 1000);
        return result;
    }

    private List<CompanyEntity> mapCompanyDTOtoEntity(CompanyDTO[] companyDTOs)
        throws JsonProcessingException {
        TypeReference<List<CompanyEntity>> entityListTypeRef = new TypeReference<>() {
        };

        var dtoListJson = objectMapper.writeValueAsString(companyDTOs);

        return objectMapper.readValue(dtoListJson, entityListTypeRef);
    }

    @SneakyThrows
    public void saveAllPriceData(List<PriceDTO> priceDTOS) {
        var entityList = mapPriceDTOtoEntity(priceDTOS);
        priceRepository.saveAll(entityList);
    }

    @SneakyThrows
    public List<PriceDTO> findAllPriceData() {
        return mapPriceEntityToDTO(priceRepository.findAll());
    }

    private List<PriceEntity> mapPriceDTOtoEntity(List<PriceDTO> priceDTOS)
        throws JsonProcessingException {
        TypeReference<List<PriceEntity>> entityListTypeRef = new TypeReference<>() {
        };

        var dtoListJson = objectMapper.writeValueAsString(priceDTOS);

        return objectMapper.readValue(dtoListJson, entityListTypeRef);
    }

    private List<PriceDTO> mapPriceEntityToDTO(List<PriceEntity> priceEntities)
        throws JsonProcessingException {
        TypeReference<List<PriceDTO>> priceDTOs = new TypeReference<>() {
        };

        var entities = objectMapper.writeValueAsString(priceEntities);

        return objectMapper.readValue(entities, priceDTOs);
    }

}

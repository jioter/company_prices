package com.parse.job;

import com.parse.dto.PriceDTO;
import com.parse.services.ParseDataService;
import java.util.Comparator;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CompanyJob {

    private final ParseDataService parseDataService;

    @Scheduled(cron = "*/5 * * * * *")
    public void runCron() {
        final var companiesData = parseDataService.getCompaniesData();
        parseDataService.saveAllCompanyData(companiesData);

        final var companyPrices = parseDataService.getCompanyPrices();
        parseDataService.saveAllPriceData(companyPrices);

        final var result = parseDataService.findAllPriceData().stream()
            .sorted(Comparator.comparingDouble(PriceDTO::getLatestPrice)).limit(5).collect(
                Collectors.toList());
        System.out.println(result);
    }

}

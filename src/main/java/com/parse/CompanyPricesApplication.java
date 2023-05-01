package com.parse;

import com.parse.services.ParseDataService;
import com.parse.utils.ApplicationContextProvider;
import java.util.TimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CompanyPricesApplication implements CommandLineRunner {

    @Autowired
    ApplicationContextProvider applicationContextProvider;

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

        SpringApplication.run(CompanyPricesApplication.class, args);
    }

    @Override
    public void run(String... args) {
        ParseDataService parseDataService = applicationContextProvider.getApplicationContext()
            .getBean(
                ParseDataService.class);
        final var companiesData = parseDataService.getCompaniesData();
        parseDataService.saveAllCompanies(companiesData);
        System.out.println(companiesData);

        final var companyPrices = parseDataService.getCompanyPrices();
        System.out.println(companyPrices);
    }

}

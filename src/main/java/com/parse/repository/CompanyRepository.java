package com.parse.repository;

import com.parse.entities.Company;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;

public interface CompanyRepository extends JpaRepositoryImplementation<Company, UUID> {

    @Query("select c.symbol from Company c")
    List<String> findAllCompanySymbols();

}

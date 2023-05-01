package com.parse.repository;

import com.parse.entities.CompanyEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;

public interface CompanyRepository extends JpaRepositoryImplementation<CompanyEntity, UUID> {

    @Query("select c.symbol from CompanyEntity c")
    List<String> findAllCompanySymbols();

}

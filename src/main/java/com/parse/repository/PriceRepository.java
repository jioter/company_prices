package com.parse.repository;

import com.parse.entities.PriceEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;

public interface PriceRepository extends JpaRepositoryImplementation<PriceEntity, UUID> {

}

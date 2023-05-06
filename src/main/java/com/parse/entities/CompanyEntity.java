package com.parse.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Builder
@Data
@Entity
@Table(name = "company")
@NoArgsConstructor
@AllArgsConstructor
public class CompanyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "company_id")
    @EqualsAndHashCode.Exclude
    private Long companyId;

    @Column(name = "date")
    private String date;

    @Column(name = "company_symbol")
    private String symbol;

    @Column(name = "is_enabled")
    private String isEnabled;

    @Column(name = "company_name")
    private String name;

    @OneToMany(mappedBy = "priceId")
    @Column(name = "prices")
    @EqualsAndHashCode.Exclude
    private List<PriceEntity> prices;

}

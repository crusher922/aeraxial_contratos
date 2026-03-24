package com.aeraxial.masterdata.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "contract_space")
public class ContractSpace extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contract_space_id")
    public Long id;

    @Column(name = "contract_id", nullable = false)
    public Long cotractId;

    @Column(name = "space_id", nullable = false)
    public Long spaceId;

    @Column(name = "effective_from")
    public LocalDate effectiveFrom;

    @Column(name = "effective_to")
    public LocalDate effectiveTo;
}

package com.aeraxial.contracts.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

@Entity
@Table(name = "rent_escalation")
public class RentEscalation extends PanacheEntityBase {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "escalation_id")
    public Long id;

    @Column(name = "contract_id", nullable = false)
    public Long contractId;

    @Column(name = "formula", length = 1000)
    public String formula;

    @Column(name = "index_name", length = 200)
    public String indexName;

    @Column(name = "periodicity", length = 20)
    public String periodicity;

    @Column(name = "extra_1", length = 500)
    public String extra1;
    @Column(name = "extra_2", length = 500)
    public String extra2;
    @Column(name = "extra_3", length = 500)
    public String extra3;
    @Column(name = "extra_4", length = 500)
    public String extra4;
    @Column(name = "extra_5", length = 500)
    public String extra5;

    @Column(name = "notes", length = 2000)
    public String notes;
}

package com.aeraxial.masterdata.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

@Entity
@Table(name = "contract_party")
public class ContractParty extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contract_party_id")
    public Long id;

    @Column(name = "contract_id", nullable = false)
    public Long contractId;

    @Column(name = "party_id", nullable = false)
    public  Long partyId;

    @Column(name = "role", nullable = false, length = 30)
    public String role;

    @Column(name = "is_primary", length = 1)
    public String isPrimary = "N";
}

package com.aeraxial.masterdata.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "leased_space")
public class LeasedSpace extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "space_id")
    public Long id;

    @Column(name = "client_id", nullable = false)
    public Long clientId;

    @Column(name = "site_id")
    public Long siteId;

    @Column(name = "location_id")
    public Long locationId;

    @Column(name = "space_code", nullable = false, length = 80)
    public String spaceCode;

    @Column(name = "area_value", precision = 12, scale = 2)
    public BigDecimal areaValue;

    @Column(name = "area_unit", length = 10)
    public String areaUnit;

    @Column(name = "space_type", nullable = false, length = 30)
    public String spaceType;

    @Column(name = "description")
    public String description;

    @Column(name = "territorial_exclusivity", length = 1)
    public String territorialExclusivity;

    @Column(name = "exclusivity_notes", length = 2000)
    public String exclusivityNotes;
}

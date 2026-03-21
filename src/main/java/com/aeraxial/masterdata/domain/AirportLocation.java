package com.aeraxial.masterdata.domain;

import io.quarkus.Generated;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

@Entity
@Table(name = "airport_location")
public class AirportLocation extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "location_id")
    public Long id;

    @Column(name = "client_id", nullable = false)
    public Long clientId;

    @Column(name = "site_id")
    public Long siteId;

    @Column(name = "terminal", length = 50)
    public String terminal;

    @Column(name = "floor", length = 20)
    public String floor;

    @Column(name = "zone", length = 80)
    public String zone;

    @Column(name = "description", length = 250)
    public String description;
}

package com.diner.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "location")
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int location_id;

    @Lob
    private String address;
    private String longitude;
    private String latitude;
    private String city;
    private String state;
    private String country;
    private Long zip;

    private String ownerUserName;

}

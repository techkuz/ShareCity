package com.hack.junction.sharecity.payload.test;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class AppUserProfile {
    private Long id;
    private String bytomId;
    private String username;
    private String name;
    private Double balance;
    private String shortDescription;
    private String description;
    private Date founded;
    private String website;
    private String city;
    private String country;
    private String roleName;
}

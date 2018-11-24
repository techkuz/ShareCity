package com.hack.junction.sharecity.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "logos")
public class Logo {
    @Id
    private Long id;

    private String imageServiceUrl;

    private String fileName;

    public Logo() {}

}

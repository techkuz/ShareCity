package com.hack.junction.sharecity.model;

import lombok.Data;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "appusers")
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String bytomId;

    @NotBlank
    @Size(max = 40)
    private String name;

    @NotBlank
    @Size(max = 20)
    private String username;

    @NaturalId
    @NotBlank
    @Size(max = 40)
    @Email
    private String email;

    @NotBlank
    @Size(max = 100)
    private String password;

    private Double balance;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "appuser_roles",
               joinColumns = @JoinColumn(name = "appuser_id"),
               inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @Size(max = 60)
    private String shortDescription;

    @Size(min = 60)
    private String description;

    private Date founded;

    private String website;

    private String city;

    private String country;

    private Boolean published;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "appuser_industries",
               joinColumns = @JoinColumn(name = "appuser_id"),
               inverseJoinColumns = @JoinColumn(name = "industry_id"))
    private Set<Industry> industries = new HashSet<>();

    public AppUser() {}

    public AppUser(String bytomId, String name, String username, String email, String password, Double balance) {
        this.bytomId = bytomId;
        this.name = name;
        this.username = username;
        this.email = email;
        this.password = password;
        this.balance = balance;
    }

}

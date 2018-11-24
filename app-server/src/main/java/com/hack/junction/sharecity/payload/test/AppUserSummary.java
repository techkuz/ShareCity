package com.hack.junction.sharecity.payload.test;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AppUserSummary {
    private Long id;
    private String name;
    private String roleName;
}

package com.hack.junction.bytom.payload;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class BusinessProfile {
    private Long id;
    private String businessName;
    private String name;
    private Instant joinedAt;
    private Long pollCount;
    private String roleName;
}

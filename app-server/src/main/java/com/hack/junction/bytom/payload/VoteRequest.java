package com.hack.junction.bytom.payload;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class VoteRequest {
    @NotNull
    private Long choiceId;
}

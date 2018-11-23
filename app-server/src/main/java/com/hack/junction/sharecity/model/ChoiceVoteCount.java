package com.hack.junction.sharecity.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChoiceVoteCount extends Vote {
    private Long choiceId;
    private Long voteCount;
}

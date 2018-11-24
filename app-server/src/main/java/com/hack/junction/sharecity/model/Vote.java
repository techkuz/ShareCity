package com.hack.junction.sharecity.model;

import com.hack.junction.sharecity.model.audit.DateAudit;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "votes", uniqueConstraints = {
        @UniqueConstraint(columnNames = {
                "poll_id",
                "appuser_id"
        })
})
public class Vote extends DateAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "poll_id", nullable = false)
    private Poll poll;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "choice_id", nullable = false)
    private Choice choice;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "appuser_id", nullable = false)
    private AppUser user;
}

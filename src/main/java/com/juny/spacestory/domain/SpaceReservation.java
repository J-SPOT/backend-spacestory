package com.juny.spacestory.domain;

import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class SpaceReservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Column(nullable = false)
    private Long fee;

    @Column(nullable = false)
    private Boolean isReserved;

    @ManyToOne
    @JoinColumn(name = "space_id")
    private Space space;

    public SpaceReservation(LocalDateTime startTime, LocalDateTime endTime, Long fee, Boolean isReserved, Space space) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.fee = fee;
        this.isReserved = isReserved;
        this.space = space;
    }
}

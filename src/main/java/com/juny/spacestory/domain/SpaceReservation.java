package com.juny.spacestory.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class SpaceReservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime reservationStart;

    @Column(nullable = false)
    private LocalDateTime reservationEnd;

    @Column(nullable = false)
    private Boolean isReserved;

    @ManyToOne
    @JoinColumn(name = "space_id")
    private Space space;

    public SpaceReservation(LocalDateTime reservationStart, LocalDateTime reservationEnd, Boolean isReserved, Space space) {
        this.reservationStart = reservationStart;
        this.reservationEnd = reservationEnd;
        this.isReserved = isReserved;
        this.space = space;
    }
}

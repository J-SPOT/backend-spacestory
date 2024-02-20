package com.juny.spacestory.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
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
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "space_id")
    private Space space;

    @OneToOne(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
    private Review review;
}

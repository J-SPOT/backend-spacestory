package com.juny.spacestory.domain;

import jakarta.persistence.*;

@Entity
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private Integer rating;

    @OneToOne
    @JoinColumn(name = "reservation_id")
    private SpaceReservation reservation;
}

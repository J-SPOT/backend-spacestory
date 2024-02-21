package com.juny.spacestory.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString()
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private Integer rating;

    @OneToOne
    @JoinColumn(name = "spaceReservation_id")
    private SpaceReservation spaceReservation;

    public Review(String content, Integer rating, SpaceReservation spaceReservation) {
        this.content = content;
        this.rating = rating;
        this.spaceReservation = spaceReservation;
    }
}

package com.juny.spacestory.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString(exclude = {"user", "space", "review"})
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
    private User user; // user_id category

    @ManyToOne
    @JoinColumn(name = "space_id")
    private Space space;

    @OneToOne(mappedBy = "spaceReservation", cascade = CascadeType.ALL, orphanRemoval = true)
    private Review review;

    public SpaceReservation(LocalDateTime reservationStart, LocalDateTime reservationEnd, Boolean isReserved, User user, Space space, Review review) {
        this.reservationStart = reservationStart;
        this.reservationEnd = reservationEnd;
        this.isReserved = isReserved;
        this.user = user;
        this.space = space;
        this.review = review;
    }

    public void setReview(Review review) {
        this.review = review;
    }
}

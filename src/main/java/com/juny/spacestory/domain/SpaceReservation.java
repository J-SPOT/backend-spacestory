package com.juny.spacestory.domain;

import com.juny.spacestory.dto.RequestUpdateReservation;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

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
    private Long userId;

    @Column(nullable = false)
    private LocalDate reservationDate;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @Column(nullable = false)
    private Long fee;

    @Column(nullable = false)
    private Boolean isReserved;

    @ManyToOne
    @JoinColumn(name = "space_id")
    private Space space;

    public SpaceReservation(Long userId, LocalDate reservationDate, LocalTime startTime, LocalTime endTime, Long fee, Boolean isReserved, Space space) {
        this.userId = userId;
        this.reservationDate = reservationDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.fee = fee;
        this.isReserved = isReserved;
        this.space = space;
    }

    public void updateReservation(RequestUpdateReservation req) {
        this.reservationDate = req.reservationDate();
        this.startTime = req.startTime();
        this.endTime = req.endTime();
        this.fee = Duration.between(startTime, endTime).toHours() * this.space.getHourlyRate();
        this.isReserved = req.isReserved();
    }
}

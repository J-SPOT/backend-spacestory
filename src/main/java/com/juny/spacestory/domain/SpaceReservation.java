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
    private Boolean isUser;

    @Column(nullable = false)
    private Boolean isReserved;

    @ManyToOne
    @JoinColumn(name = "space_id")
    private Space space;

    public SpaceReservation(Long userId, LocalDate reservationDate, LocalTime startTime, LocalTime endTime, Long fee, Boolean isUser, Boolean isReserved, Space space) {
        this.userId = userId;
        this.reservationDate = reservationDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.fee = fee;
        this.isUser = isUser;
        this.isReserved = isReserved;
        this.space = space;
    }

    public void updateReservation(RequestUpdateReservation req, User user, Host host) {
        this.reservationDate = req.reservationDate();
        this.startTime = req.startTime();
        this.endTime = req.endTime();
        long differenceAmount = getFee() - Duration.between(startTime, endTime).toHours() * this.space.getHourlyRate();
        if (req.isUser()) {
            if (differenceAmount < 0)
                user.payFee(-differenceAmount, host);
            if (differenceAmount > 0)
                user.getRefund(differenceAmount, host);
        }
        this.fee -= differenceAmount;
        this.isReserved = req.isReserved();
        if (req.isUser() && !isReserved)
            user.getRefund(getFee(), host);
    }

    public void softDelete(SpaceReservation reservation) {
        this.isReserved = false;
    }
}

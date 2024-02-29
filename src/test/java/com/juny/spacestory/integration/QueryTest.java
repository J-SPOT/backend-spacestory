package com.juny.spacestory.integration;

import com.juny.spacestory.domain.SpaceReservation;
import com.juny.spacestory.repository.ReservationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
public class QueryTest {

    @Autowired
    ReservationRepository reservationRepository;
    @DisplayName("N+1문제를 확인한다.")
    @Test
    @Transactional
    void CheckN1() {
        List<SpaceReservation> reservations1 = reservationRepository.findAll();
        System.out.println("reservations1.get(0).getSpace().getSpaceName() = " + reservations1.get(0).getSpace().getSpaceName());

        List<SpaceReservation> reservations2 = reservationRepository.findAllReservations();
        System.out.println("reservations2.size() = " + reservations2.size());
    }
}

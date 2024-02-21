package com.juny.spacestory.service;

import com.juny.spacestory.domain.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.HashSet;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceTest {

    @InjectMocks
    private ReservationService reservationService;
    @DisplayName("엔티티 연결 검사")
    @Test
    void userReserveSpace() {
        //given
        // RealEstate - Host
        Address address1 = new Address("서울 성동구 아차산로 17길 48", "서울특별시 성동구 성수2동2가 280", "서울특별시", "성동구", "성수동");
        Host host1 = new Host(1L, "host1", 0, new HashSet<>());
        RealEstate realEstate1 = new RealEstate(1L, address1, 2, false, true, host1, new HashSet<>());
        host1.getRealEstates().add(realEstate1);
        System.out.println("realEstate1.getHost().toString() = " + realEstate1.getHost().toString());
        System.out.println("host1.getRealEstates().toString() = " + host1.getRealEstates().toString());

        // Space - RealEstate
        System.out.println();
        Space space1 = new Space(SpaceType.SPORT, 50, 10, realEstate1, new HashSet<>(), new HashSet<>());
        realEstate1.getSpaces().add(space1);
        System.out.println("space1.getRealEstate().toString() = " + space1.getRealEstate().toString());
        System.out.println("realEstate1.getSpaces().toString() = " + realEstate1.getSpaces().toString());

        // SpaceTypeDetail - Space
        System.out.println();
        TypeDetail typedetails1 = new TypeDetail("세부타입1", new HashSet<>());
        TypeDetail typedetails2 = new TypeDetail("세부타입2", new HashSet<>());
        SpaceTypeDetail spaceTypeDetails1 = new SpaceTypeDetail(space1, typedetails1);
        SpaceTypeDetail spaceTypeDetails2 = new SpaceTypeDetail(space1, typedetails2);
        space1.getSpaceTypeDetails().add(spaceTypeDetails1);
        space1.getSpaceTypeDetails().add(spaceTypeDetails2);
        System.out.println("space1.getSpaceTypeDetails().toString() = " + space1.getSpaceTypeDetails().toString());

        // User - SpaceReservation,
        System.out.println();
        User user = new User("유저2", "email1@gmail.com", "닉네임1", 0, new HashSet<>());
        LocalDateTime start = LocalDateTime.of(2025, 2, 21, 9, 0);
        LocalDateTime end = LocalDateTime.of(2025, 2, 21, 11, 0);
        SpaceReservation spaceReservation1 = new SpaceReservation(start, end, false, user, space1, null);
        user.getSpaceReservations().add(spaceReservation1);
        space1.getSpaceReservations().add(spaceReservation1);
        System.out.println("user.getSpaceReservations().toString() = " + user.getSpaceReservations().toString());
        System.out.println("space1.getSpaceReservations().toString() = " + space1.getSpaceReservations().toString());

        // Review - SpaceReservation
        System.out.println();
        Review review1 = new Review("내용1", 5, spaceReservation1);
        spaceReservation1.setReview(review1);
        System.out.println("review1.toString() = " + review1.toString());
        System.out.println("spaceReservation1.toString() = " + spaceReservation1.getReview().toString());
    }
}

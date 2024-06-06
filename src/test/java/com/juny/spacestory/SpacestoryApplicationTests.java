package com.juny.spacestory;

import com.juny.spacestory.host.Host;
import com.juny.spacestory.host.HostRepository;
import com.juny.spacestory.realestate.Address;
import com.juny.spacestory.realestate.RealEstate;
import com.juny.spacestory.realestate.RealEstateRepository;
import com.juny.spacestory.reservation.repository.ReservationRepository;
import com.juny.spacestory.reservation.service.ReservationService;
import com.juny.spacestory.space.domain.DetailedType;
import com.juny.spacestory.space.domain.Space;
import com.juny.spacestory.space.repository.SpaceRepository;
import com.juny.spacestory.space.domain.SpaceType;
import com.juny.spacestory.user.repository.UserRepository;
import com.juny.spacestory.global.config.EnvConfig;
import com.juny.spacestory.user.domain.User;
import com.juny.spacestory.reservation.dto.RequestCreateReservation;
import com.juny.spacestory.reservation.dto.RequestUpdateReservation;
import com.juny.spacestory.reservation.dto.ResponseReservation;
import com.juny.spacestory.reservation.dto.TimeSlot;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@PropertySource(
    value = {
      "classpath:env.yml",
    },
    factory = EnvConfig.class)
class SpacestoryApplicationTests {

  @Autowired
  UserRepository userRepository;

  @Autowired
  HostRepository hostRepository;

  @Autowired
  RealEstateRepository realEstateRepository;

  @Autowired
  SpaceRepository spaceRepository;

  @Autowired
  ReservationRepository reservationRepository;

  @Autowired
  ReservationService reservationService;

  @DisplayName("유저가 3월 3일 공간 예약하고 수정하고 취소하고 이용 가능한 시간을 확인한다")
  @Transactional
  @Test
  void reservation() {
    // 사용자 1, 2를 생성한다.
    // 호스트가 부동산을 생성한다.
    // 해당 부동산에 공간 1개를 만든다.
    // 유저1이 공간1에 대한 예약을 2개 만든다.
    // 3월 3일 9~12시 예약
    // 3월 3일 17~19시 예약
    // 3월 3일 17~19시 예약을 취소한다.
    // 3월 3일 9~12시 예약을 9~11시 예약으로 변경한다.
    // 이용가능한 시간이 11~22시인지 확인한다.
    User u1 = new User("user@1", "user@1@gmail.com", "1234");
    User user1 = userRepository.save(u1);

    Host ho = new Host("host1", 0L, null);
    Host host = hostRepository.save(ho);

    Address address = new Address("서울시 강북구 강북로 27길 30", "서울시 강북구 수유동 27-30", "서울특별시", "강북구", "수유동");
    RealEstate res = new RealEstate(address, 2, false, false, false, host);
    RealEstate realEstate = realEstateRepository.save(res);

    HashSet<DetailedType> details = new HashSet<>();
    details.add(DetailedType.PARTY_ROOM);
    details.add(DetailedType.RESIDENCE);
    Space sp =
        new Space(
            SpaceType.FRIENDSHIP,
            "space1",
            LocalTime.of(9, 0),
            LocalTime.of(22, 0),
            0,
            17,
            5,
            "상세설명",
            false,
            details,
            realEstate);
    spaceRepository.save(sp);
    Space space = spaceRepository.save(sp);
    RequestCreateReservation req1 =
        new RequestCreateReservation(
            user1.getId(), LocalDate.of(2024, 3, 3), LocalTime.of(9, 0), LocalTime.of(12, 0), true);
    RequestCreateReservation req2 =
        new RequestCreateReservation(
            user1.getId(),
            LocalDate.of(2024, 3, 3),
            LocalTime.of(17, 0),
            LocalTime.of(19, 0),
            true);
    ResponseReservation reservation1 = reservationService.reserve(space.getId(), req1);
    ResponseReservation reservation2 = reservationService.reserve(space.getId(), req2);

    RequestUpdateReservation req =
        new RequestUpdateReservation(
            user1.getId(),
            space.getId(),
            LocalDate.of(2024, 3, 3),
            LocalTime.of(9, 0),
            LocalTime.of(11, 0),
            true);
    reservationService.update(reservation1.id(), req);

    reservationService.delete(reservation2.id());

    List<TimeSlot> availableReservation =
        reservationService.getAvailableReservation(space.getId(), LocalDate.of(2024, 3, 3));
    List<TimeSlot> expected =
        List.of(
            new TimeSlot(LocalTime.of(11, 0), LocalTime.of(12, 0)),
            new TimeSlot(LocalTime.of(12, 0), LocalTime.of(13, 0)),
            new TimeSlot(LocalTime.of(13, 0), LocalTime.of(14, 0)),
            new TimeSlot(LocalTime.of(14, 0), LocalTime.of(15, 0)),
            new TimeSlot(LocalTime.of(15, 0), LocalTime.of(16, 0)),
            new TimeSlot(LocalTime.of(16, 0), LocalTime.of(17, 0)),
            new TimeSlot(LocalTime.of(17, 0), LocalTime.of(18, 0)),
            new TimeSlot(LocalTime.of(18, 0), LocalTime.of(19, 0)),
            new TimeSlot(LocalTime.of(19, 0), LocalTime.of(20, 0)),
            new TimeSlot(LocalTime.of(20, 0), LocalTime.of(21, 0)),
            new TimeSlot(LocalTime.of(21, 0), LocalTime.of(22, 0)));
    assertThat(availableReservation).isNotNull();
    assertThat(availableReservation).usingRecursiveComparison().isEqualTo(expected);
  }
}

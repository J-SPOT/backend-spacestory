package com.juny.spacestory.service;

import com.juny.spacestory.domain.*;
import com.juny.spacestory.repository.HostRepository;
import com.juny.spacestory.repository.ReservationRepository;
import com.juny.spacestory.repository.SpaceRepository;
import com.juny.spacestory.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith; import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceTest {

    @InjectMocks
    private ReservationService reservationService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SpaceRepository spaceRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private HostRepository hostRepository;

    private User user1, user2;
    private Host host1, host2;
    private RealEstate realEstate1, realEstate2;
    private Space space1, space2;

    @BeforeEach
    void setUp() {
        user1 = new User("user1", "user1@gmail.com", "nickname1", 100_000L);
        user2 = new User("user1", "user1@gmail.com", "nickname1", 0L);

        host1 = new Host("host1", 0L);
        host2 = new Host("host2", 0L);

        Address address1 = new Address("서울특별시 성동구 아차산로 17길 48", "서울특별시 성동구 성수동2가 280 성수 SK V1 CENTER 1", "서울특별시", "성동구", "성수동");
        Address address2 = new Address("서울특별시 성동구 고자산로 17길 9-1", "서울특별시 성동구 마장동 564-7", "서울특별시", "성동구", "마장동");
        realEstate1 = new RealEstate(address1, 2, false, true, host1);
        realEstate2 = new RealEstate(address2, 5, true, true, host2);

        DetailedType lecture = new DetailedType("강의실");
        DetailedType meeting = new DetailedType("회의실");
        HashSet<DetailedType> detailedType1 = new HashSet<>();
        detailedType1.add(lecture);
        detailedType1.add(meeting);
        space1 = new Space(SpaceType.EVENT, "소규모 회의, 업무 공간", 30000, 55, 10, realEstate1, detailedType1);
        space2 = new Space(SpaceType.EVENT, "중규모 회의, 업무 공간", 60000, 55, 10, realEstate1, detailedType1);
    }

    @DisplayName("사용자가 공간을 예약 시 예약된 공간을 반환한다")
    @Test
    void ReserveSpace() {
        //given
        Long userId = user1.getId();
        Long spaceId = space1.getId();
        LocalDateTime start = LocalDateTime.of(2024, 3, 3, 9, 0);
        LocalDateTime end = LocalDateTime.of(2024, 3, 3, 11, 0);
        long usageTime = Duration.between(start, end).toHours();
        long usageFee = space1.getHourlyRate() * usageTime;
        SpaceReservation expected = new SpaceReservation(start, end, usageFee, true, space1);

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user1));
        Mockito.when(spaceRepository.findById(spaceId)).thenReturn(Optional.of(space1));
        Mockito.when(reservationRepository.findBySpaceIdAndEndTimeAfter(Mockito.eq(spaceId), Mockito.any(LocalDateTime.class))).thenReturn(Collections.emptyList());
        Mockito.when(reservationRepository.save(Mockito.any(SpaceReservation.class))).thenReturn(expected);

        //when
        SpaceReservation actual = reservationService.reserveSpace(userId, spaceId, start, end);

        //then
        assertThat(actual).isEqualTo(expected);
        Mockito.verify(reservationRepository).save(Mockito.any(SpaceReservation.class));
        assertThat(user1.getPoint()).isEqualTo(40_000L);
        assertThat(host1.getPoint()).isEqualTo(60_000L);
    }

    @DisplayName("[실패] 공간 예약 시 최소 시간은 1시간 이상이어야 한다.")
    @Test
    void reserveSpace_WithLessThanOneHour() {
        //given
        Long userId = user1.getId();
        Long spaceId = space1.getId();
        LocalDateTime start = LocalDateTime.of(2024, 3, 3, 9, 0);
        LocalDateTime end = LocalDateTime.of(2024, 3, 3, 9, 0);

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user1));
        Mockito.when(spaceRepository.findById(spaceId)).thenReturn(Optional.of(space1));

        //when

        //then
        assertThatThrownBy(() -> {
            reservationService.reserveSpace(userId, spaceId, start, end);
        }).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("공간 예약은 최소 1시간입니다.");
    }

    @DisplayName("[실패] 공간 예약 시 이미 예약된 공간은 예약할 수 없다. 9~12시 예약이 있을 경우 7~10시 예약은 실패한다.")
    @Test
    void reserveSpace_withOverlappedReservationTime_1() {
        Long userId = user1.getId();
        Long spaceId = space1.getId();

        LocalDateTime start = LocalDateTime.of(2024, 3, 3, 9, 0);
        LocalDateTime end = LocalDateTime.of(2024, 3, 3, 12, 0);
        long usageTime = Duration.between(start, end).toHours();
        long usageFee = space1.getHourlyRate() * usageTime;
        SpaceReservation reservation = new SpaceReservation(start, end, usageFee, true, space1);
        List<SpaceReservation> validReservations = new ArrayList<>();
        validReservations.add(reservation);

        Long reqUserId = user2.getId();
        Long reqSpaceId = space1.getId();
        LocalDateTime reqStart = LocalDateTime.of(2024, 3, 3, 7, 0);
        LocalDateTime reqEnd = LocalDateTime.of(2024, 3, 3, 10, 0);

        Mockito.when(userRepository.findById(reqUserId)).thenReturn(Optional.of(user2));
        Mockito.when(spaceRepository.findById(reqSpaceId)).thenReturn(Optional.of(space1));
        Mockito.when(reservationRepository.findBySpaceIdAndEndTimeAfter(Mockito.eq(reqSpaceId), Mockito.any(LocalDateTime.class))).thenReturn(validReservations);

        //when

        //then
        assertThatThrownBy(() -> {
            reservationService.reserveSpace(reqUserId, reqSpaceId, reqStart, reqEnd);
        }).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("이미 예약된 공간입니다.");
    }

    @DisplayName("[실패] 공간 예약 시 이미 예약된 공간은 예약할 수 없다. 9~12시 예약이 있을 경우 11~14시 예약은 실패한다.")
    @Test
    void reserveSpace_withOverlappedReservationTime_2() {
        Long userId = user1.getId();
        Long spaceId = space1.getId();

        LocalDateTime start = LocalDateTime.of(2024, 3, 3, 9, 0);
        LocalDateTime end = LocalDateTime.of(2024, 3, 3, 12, 0);
        long usageTime = Duration.between(start, end).toHours();
        long usageFee = space1.getHourlyRate() * usageTime;
        SpaceReservation reservation = new SpaceReservation(start, end, usageFee, true, space1);
        List<SpaceReservation> validReservations = new ArrayList<>();
        validReservations.add(reservation);

        Long reqUserId = user2.getId();
        Long reqSpaceId = space1.getId();
        LocalDateTime reqStart = LocalDateTime.of(2024, 3, 3, 11, 0);
        LocalDateTime reqEnd = LocalDateTime.of(2024, 3, 3, 14, 0);

        Mockito.when(userRepository.findById(reqUserId)).thenReturn(Optional.of(user2));
        Mockito.when(spaceRepository.findById(reqSpaceId)).thenReturn(Optional.of(space1));
        Mockito.when(reservationRepository.findBySpaceIdAndEndTimeAfter(Mockito.eq(reqSpaceId), Mockito.any(LocalDateTime.class))).thenReturn(validReservations);

        //when

        //then
        assertThatThrownBy(() -> {
            reservationService.reserveSpace(reqUserId, reqSpaceId, reqStart, reqEnd);
        }).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("이미 예약된 공간입니다.");
    }

    @DisplayName("[실패] 공간 예약 시 포인트가 부족하면 예약할 수 없다.")
    @Test
    void reserveSpace_withNotEnoughPoint() {
        //given
        Long userId = user2.getId();
        Long spaceId = space1.getId();
        LocalDateTime start = LocalDateTime.of(2024, 3, 3, 9, 0);
        LocalDateTime end = LocalDateTime.of(2024, 3, 3, 11, 0);
        long usageTime = Duration.between(start, end).toHours();
        long usageFee = space1.getHourlyRate() * usageTime;
        SpaceReservation expected = new SpaceReservation(start, end, usageFee, true, space1);

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user2));
        Mockito.when(spaceRepository.findById(spaceId)).thenReturn(Optional.of(space1));

        //when

        //then
        assertThatThrownBy(() -> {
            reservationService.reserveSpace(userId, spaceId, start, end);
        }).isInstanceOf(RuntimeException.class).hasMessageContaining("사용자의 포인트가 부족합니다.");
    }
}

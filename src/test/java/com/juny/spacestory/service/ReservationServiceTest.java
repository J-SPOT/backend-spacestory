package com.juny.spacestory.service;

import com.juny.spacestory.domain.*;
import com.juny.spacestory.dto.RequestUpdateReservation;
import com.juny.spacestory.dto.TimeSlot;
import com.juny.spacestory.repository.HostRepository;
import com.juny.spacestory.repository.ReservationRepository;
import com.juny.spacestory.repository.SpaceRepository;
import com.juny.spacestory.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith; import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

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
    private Host host;
    private RealEstate realEstate;
    private Space space;

    @BeforeEach
    void setUp() {
        user1 = new User("user1", "user1@gmail.com", "nickname1", 100_000L);
        user2 = new User("user1", "user1@gmail.com", "nickname1", 0L);
        host = new Host("host1", 0L);
        Address address1 = new Address("서울특별시 성동구 아차산로 17길 48", "서울특별시 성동구 성수동2가 280 성수 SK V1 CENTER 1", "서울특별시", "성동구", "성수동");
        realEstate = new RealEstate(address1, 2, false, true, host);

        HashSet<DetailedType> detailedTypes = new HashSet<>();
        detailedTypes.add(DetailedType.lectureRoom);
        detailedTypes.add(DetailedType.meetingRoom);
        space = new Space(SpaceType.EVENT, "소규모 회의, 업무 공간", LocalTime.of(9, 0), LocalTime.of(22, 0), 30000, 55, 10, detailedTypes, realEstate);
    }

    @DisplayName("사용자가 공간을 예약 시 예약된 공간을 반환한다")
    @Test
    void Reserve() {
        //given
        Long userId = user1.getId();
        Long spaceId = space.getId();
        LocalDate reservationDate = LocalDate.of(2024, 3, 3);
        LocalTime start = LocalTime.of(9, 0);
        LocalTime end = LocalTime.of( 11, 0);
        long usageTime = Duration.between(start, end).toHours();
        long usageFee = space.getHourlyRate() * usageTime;
        SpaceReservation expected = new SpaceReservation(userId, reservationDate, start, end, usageFee, true, space);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user1));
        when(spaceRepository.findById(spaceId)).thenReturn(Optional.of(space));
        when(reservationRepository.findBySpaceIdAndReservationDateAndIsReservedTrue(eq(spaceId), any(LocalDate.class))).thenReturn(Collections.emptyList());
        when(reservationRepository.save(any(SpaceReservation.class))).thenReturn(expected);
        when(hostRepository.save(space.getRealEstate().getHost())).thenReturn(host);

        //when
        SpaceReservation spaceReservation = reservationService.reserve(userId, spaceId, reservationDate, start, end);

        //then
        assertThat(spaceReservation).isNotNull();
        assertThat(spaceReservation).isEqualTo(expected);
        verify(reservationRepository).save(any(SpaceReservation.class));
        assertThat(user1.getPoint()).isEqualTo(40_000L);
        assertThat(host.getPoint()).isEqualTo(60_000L);
    }

    @DisplayName("[실패] 공간 예약 시 최소 시간은 1시간 이상이어야 한다.")
    @Test
    void Reserve_WithLessThanOneHour() {
        //given
        Long userId = user1.getId();
        Long spaceId = space.getId();
        LocalDate reservationDate = LocalDate.of(2024, 3, 3);
        LocalTime start = LocalTime.of(9, 0);
        LocalTime end = LocalTime.of(9, 0);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user1));
        when(spaceRepository.findById(spaceId)).thenReturn(Optional.of(space));

        //when

        //then
        assertThatThrownBy(() -> reservationService.reserve(userId, spaceId, reservationDate, start, end))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("space reservations require a minimum of 1 hour.");
    }

    @DisplayName("[실패] 공간 예약 시 이미 예약된 공간은 예약할 수 없다. 9~12시 예약이 있을 경우 7~10시 예약은 실패한다.")
    @Test
    void Reserve_WithOverlappedReservationTime_1() {
        Long userId = user1.getId();
        LocalDate reservationDate = LocalDate.of(2024, 3, 3);
        LocalTime start = LocalTime.of(9, 0);
        LocalTime end = LocalTime.of( 12, 0);
        long usageTime = Duration.between(start, end).toHours();
        long usageFee = space.getHourlyRate() * usageTime;
        SpaceReservation reservation = new SpaceReservation(userId, reservationDate, start, end, usageFee, true, space);
        List<SpaceReservation> validReservations = new ArrayList<>();
        validReservations.add(reservation);

        Long reqUserId = user2.getId();
        Long reqSpaceId = space.getId();
        LocalTime reqStart = LocalTime.of( 7, 0);
        LocalTime reqEnd = LocalTime.of(10, 0);

        when(userRepository.findById(reqUserId)).thenReturn(Optional.of(user2));
        when(spaceRepository.findById(reqSpaceId)).thenReturn(Optional.of(space));
        when(reservationRepository.findBySpaceIdAndReservationDateAndIsReservedTrue(eq(reqSpaceId), any(LocalDate.class))).thenReturn(validReservations);

        //when

        //then
        assertThatThrownBy(() -> reservationService.reserve(reqUserId, reqSpaceId, reservationDate, reqStart, reqEnd))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("space is already reserved.");
    }

    @DisplayName("[실패] 공간 예약 시 이미 예약된 공간은 예약할 수 없다. 9~12시 예약이 있을 경우 11~14시 예약은 실패한다.")
    @Test
    void Reserve_WithOverlappedReservationTime_2() {
        Long userId = user1.getId();
        LocalDate reservationDate = LocalDate.of(2024, 3, 3);
        LocalTime start = LocalTime.of(9, 0);
        LocalTime end = LocalTime.of(12, 0);
        long usageTime = Duration.between(start, end).toHours();
        long usageFee = space.getHourlyRate() * usageTime;
        SpaceReservation reservation = new SpaceReservation(userId, reservationDate, start, end, usageFee, true, space);
        List<SpaceReservation> validReservations = new ArrayList<>();
        validReservations.add(reservation);

        Long reqUserId = user2.getId();
        Long reqSpaceId = space.getId();
        LocalTime reqStart = LocalTime.of(11, 0);
        LocalTime reqEnd = LocalTime.of(14, 0);

        when(userRepository.findById(reqUserId)).thenReturn(Optional.of(user2));
        when(spaceRepository.findById(reqSpaceId)).thenReturn(Optional.of(space));
        when(reservationRepository.findBySpaceIdAndReservationDateAndIsReservedTrue(eq(reqSpaceId), any(LocalDate.class))).thenReturn(validReservations);

        //when

        //then
        assertThatThrownBy(() -> reservationService.reserve(reqUserId, reqSpaceId, reservationDate, reqStart, reqEnd))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("space is already reserved.");
    }

    @DisplayName("[실패] 공간 예약 시 포인트가 부족하면 예약할 수 없다.")
    @Test
    void Reserve_WithNotEnoughPoint() {
        //given
        Long userId = user2.getId();
        Long spaceId = space.getId();
        LocalDate reservationDate = LocalDate.of(2024, 3, 3);
        LocalTime start = LocalTime.of(9, 0);
        LocalTime end = LocalTime.of(11, 0);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user2));
        when(spaceRepository.findById(spaceId)).thenReturn(Optional.of(space));

        //when

        //then
        assertThatThrownBy(() -> reservationService.reserve(userId, spaceId, reservationDate, start, end))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("user's points are insufficient");
    }

    @DisplayName("사용자가 특정 날짜에 예약할 수 있는 시간을 조회하다. 운영시간은 9~22시, 예약은 9~12시, 14~15시에 있다.")
    @Test
    void GetAvaliableReservation() {
        //given
        Long spaceId = space.getId();
        LocalDate reservationDate = LocalDate.of(2024, 3, 3);
        LocalTime start = LocalTime.of(9, 0);
        LocalTime end = LocalTime.of(12, 0);
        long usageTime = Duration.between(start, end).toHours();
        long usageFee = space.getHourlyRate() * usageTime;
        SpaceReservation reservation = new SpaceReservation(user1.getId(), reservationDate, start, end, usageFee, true, space);

        LocalTime start2 = LocalTime.of(14, 0);
        LocalTime end2 = LocalTime.of(15, 0);
        long usageTime2 = Duration.between(start2, end2).toHours();
        long usageFee2 = space.getHourlyRate() * usageTime2;
        SpaceReservation reservation2 = new SpaceReservation(user1.getId(), reservationDate, start2, end2, usageFee2, true, space);

        List<SpaceReservation> validReservations = new ArrayList<>();
        validReservations.add(reservation);
        validReservations.add(reservation2);

        when(spaceRepository.findById(spaceId)).thenReturn(Optional.of(space));
        when(reservationRepository.findBySpaceIdAndReservationDateAndIsReservedTrue(spaceId, reservationDate)).thenReturn(validReservations);

        //when
        List<TimeSlot> availableReservation = reservationService.getAvailableReservation(spaceId, reservationDate);

        //then
        List<TimeSlot> expected = List.of(
                new TimeSlot(LocalTime.of(12, 0), LocalTime.of(13, 0)),
                new TimeSlot(LocalTime.of(13, 0), LocalTime.of(14, 0)),
                new TimeSlot(LocalTime.of(15, 0), LocalTime.of(16, 0)),
                new TimeSlot(LocalTime.of(16, 0), LocalTime.of(17, 0)),
                new TimeSlot(LocalTime.of(17, 0), LocalTime.of(18, 0)),
                new TimeSlot(LocalTime.of(18, 0), LocalTime.of(19, 0)),
                new TimeSlot(LocalTime.of(19, 0), LocalTime.of(20, 0)),
                new TimeSlot(LocalTime.of(20, 0), LocalTime.of(21, 0)),
                new TimeSlot(LocalTime.of(21, 0), LocalTime.of(22, 0))
                );
        assertThat(availableReservation).isNotNull();
        assertThat(availableReservation).usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("사용자가 자신의 예약 목록을 조회한다.")
    @Test
    void GetUserReservation() {
        //given
        Long userId = user1.getId();
        LocalDate reservationDate = LocalDate.of(2024, 3, 3);
        LocalTime start = LocalTime.of(9, 0);
        LocalTime end = LocalTime.of(12, 0);
        long usageTime = Duration.between(start, end).toHours();
        long usageFee = space.getHourlyRate() * usageTime;
        SpaceReservation reservation = new SpaceReservation(userId, reservationDate, start, end, usageFee, true, space);

        LocalTime start2 = LocalTime.of(14, 0);
        LocalTime end2 = LocalTime.of(15, 0);
        long usageTime2 = Duration.between(start2, end2).toHours();
        long usageFee2 = space.getHourlyRate() * usageTime2;
        SpaceReservation reservation2 = new SpaceReservation(userId, reservationDate, start2, end2, usageFee2, true, space);

        List<SpaceReservation> expected = new ArrayList<>();
        expected.add(reservation);
        expected.add(reservation2);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user1));
        when(reservationRepository.findByUserId(userId)).thenReturn(expected);

        //when
        List<SpaceReservation> userReservation = reservationService.getReservationsByUserId(userId);

        //then
        assertThat(userReservation).isNotNull();
        assertThat(userReservation).usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("사용자가 예약정보를 같은 날 다른 시간으로 변경한다. 9~12시 예약 -> 9~11시 예약으로 변경한다.")
    @Test
    void UpdateReservation() {
        //given
        Long userId = user1.getId();
        Long spaceId = space.getId();
        LocalDate reservationDate = LocalDate.of(2024, 3, 3);
        LocalTime start = LocalTime.of(9, 0);
        LocalTime end = LocalTime.of(12, 0);
        long usageTime = Duration.between(start, end).toHours();
        long usageFee = space.getHourlyRate() * usageTime;
        SpaceReservation reservation = new SpaceReservation(userId, reservationDate, start, end, usageFee, true, space);
        List<SpaceReservation> reservations = new ArrayList<>();
        reservations.add(reservation);

        Long reservationId = reservation.getId();
        RequestUpdateReservation requestUpdateReservation = new RequestUpdateReservation(userId, spaceId, LocalDate.of(2024, 3, 3), LocalTime.of(9, 0), LocalTime.of(11, 0), true);
        LocalTime start2 = LocalTime.of(9, 0);
        LocalTime end2 = LocalTime.of(11, 0);
        long usageTime2 = Duration.between(start2, end2).toHours();
        long usageFee2 = space.getHourlyRate() * usageTime2;
        SpaceReservation expected = new SpaceReservation(userId, reservationDate, start2, end2, usageFee2, true, space);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user1));
        when(spaceRepository.findById(spaceId)).thenReturn(Optional.of(space));
        when(reservationRepository.findById(reservation.getId())).thenReturn(Optional.of(reservation));
        when(reservationRepository.findBySpaceIdAndReservationDateAndIsReservedTrue(spaceId, reservationDate)).thenReturn(reservations);
        when(reservationRepository.save(any(SpaceReservation.class))).thenReturn(expected);

        //when
        SpaceReservation update = reservationService.update(userId, spaceId, reservationId, requestUpdateReservation);

        //then
        assertThat(update).isNotNull();
        assertThat(update).usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("예약 정보를 삭제를 삭제한다.")
    @Test
    void DeleteReservation_WithValidReservationId() {
        //given
        Long userId = user1.getId();
        LocalDate reservationDate = LocalDate.of(2024, 3, 3);
        LocalTime start = LocalTime.of(9, 0);
        LocalTime end = LocalTime.of(12, 0);
        long usageTime = Duration.between(start, end).toHours();
        long usageFee = space.getHourlyRate() * usageTime;
        SpaceReservation reservation = new SpaceReservation(userId, reservationDate, start, end, usageFee, true, space);
        Long reservationId = reservation.getId();
        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));

        //when
        reservationService.delete(reservationId);

        //then
        verify(reservationRepository).delete(reservation);
    }
}

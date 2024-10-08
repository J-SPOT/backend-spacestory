package com.juny.spacestory.service;

//import com.juny.spacestory.global.exception.ErrorCode;
//import com.juny.spacestory.space.dto.RequestCreateSpace;
//import com.juny.spacestory.space.dto.RequestUpdateSpace;
//import com.juny.spacestory.space.dto.ResSpaces;
//import com.juny.spacestory.global.exception.hierarchy.space.SpaceInvalidDetailedTypeBusinessException;
//import com.juny.spacestory.host.Host;
//import com.juny.spacestory.space.mapper.SpaceMapper;
//import com.juny.spacestory.space.realestate.Address;
//import com.juny.spacestory.space.realestate.RealEstate;
//import com.juny.spacestory.space.realestate.RealEstateRepository;
//import com.juny.spacestory.space.repository.SpaceRepository;
//import com.juny.spacestory.space.domain.DetailedType;
//import com.juny.spacestory.space.domain.Space;
//import com.juny.spacestory.space.domain.SpaceType;
//import com.juny.spacestory.space.service.SpaceService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mapstruct.factory.Mappers;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.test.util.ReflectionTestUtils;
//import java.time.LocalTime;
//import java.util.EnumSet;
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.*;
//import static org.mockito.Mockito.*;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//public class SpaceServiceTest {
//
//    private final SpaceMapper mapper = Mappers.getMapper(SpaceMapper.class);
//    @Mock
//    SpaceRepository spaceRepository;
//
//    @Mock
//    RealEstateRepository realEstateRepository;
//    @InjectMocks
//    private SpaceService spaceService;
//    private RealEstate realEstate;
//
//    private Space space;
//
//    @BeforeEach
//    void setUp() {
//        ReflectionTestUtils.setField(spaceService, "mapper", mapper);
//        Host host = new Host("host1", 0L, null);
//        realEstate = new RealEstate(new Address("도로명 주소1", "지번 주소1", "서울특별시", "강남구", "서초동"), 2, false, false, false, host);
//    }
//
//    @DisplayName("공간 정보를 등록한다.")
//    @Test
//    void CreateSpace() {
//        //given
//        RequestCreateSpace req = new RequestCreateSpace(SpaceType.ART, "Art place", LocalTime.of(10, 0), LocalTime.of(16, 0), 10000, 15, 5, "또 오고 싶은 예술 공간", false, EnumSet.of(DetailedType.DANCE_ROOM, DetailedType.VOCAL_ROOM), "상세 도로명 주소1", "상세 지번 주소2", "서울특별시", "강남구", "서초동", 2, false, false, 1L);
//        Space expectedSpace = new Space(SpaceType.ART, "Art", LocalTime.of(10, 0), LocalTime.of(16, 0), 10000, 15, 5, "또 오고 싶은 예술 공간", false, EnumSet.of(DetailedType.DANCE_ROOM, DetailedType.VOCAL_ROOM), realEstate);
//        ResSpaces expected = mapper.SpaceToResponseSpace(expectedSpace);
//
//        when(realEstateRepository.findByAddress_RoadAddress(req.roadAddress())).thenReturn(Optional.of(realEstate));
//        when(spaceRepository.save(any(Space.class))).thenReturn(expectedSpace);
//
//        //when
//        ResSpaces space = spaceService.create(req);
//
//        //then
//        assertThat(space).isNotNull();
//        assertThat(space).usingRecursiveComparison().isEqualTo(expected);
//    }
//
//    @DisplayName("[실패] 공간 타입에 맞지 않는 세부 타입 시 공간 생성을 실패한다. ART 세부 타입으로 STUDY_ROOM")
//    @Test
//    void CreateSpaceWithInvalidDetailedType() {
//        //given
//        RequestCreateSpace req = new RequestCreateSpace(SpaceType.ART, "Art place", LocalTime.of(10, 0), LocalTime.of(16, 0), 10000, 15, 5, "또 오고 싶은 예술 공간", false, EnumSet.of(DetailedType.STUDY_ROOM, DetailedType.VOCAL_ROOM), "상세 도로명 주소1", "상세 지번 주소2", "서울특별시", "강남구", "서초동", 2, false, false, 1L);
//
//    // when
//
//    // then
//    assertThatThrownBy(() -> spaceService.create(req))
//        .isInstanceOf(SpaceInvalidDetailedTypeBusinessException.class)
//        .hasMessageContaining(ErrorCode.SPACE_INVALID_DETAILED_TYPE.getMsg());
//    }
//
//    @DisplayName("[실패] 공간 타입에 맞지 않는 세부 타입 시 공간 생성을 실패한다. FRIENDSHIP 세부 타입으로 DANCE_ROOM")
//    @Test
//    void CreateSpaceWithInvalidDetailedType_2() {
//        //given
//        RequestCreateSpace req = new RequestCreateSpace(SpaceType.FRIENDSHIP, "Art place", LocalTime.of(10, 0), LocalTime.of(16, 0), 10000, 15, 5, "또 오고 싶은 친목 공간", false, EnumSet.of(DetailedType.CAFE, DetailedType.DANCE_ROOM), "상세 도로명 주소1", "상세 지번 주소2", "서울특별시", "강남구", "서초동", 2, false, false, 1L);
//
//    // when
//
//    // then
//    assertThatThrownBy(() -> spaceService.create(req))
//        .isInstanceOf(SpaceInvalidDetailedTypeBusinessException.class)
//        .hasMessageContaining(ErrorCode.SPACE_INVALID_DETAILED_TYPE.getMsg());
//    }
//
//    @DisplayName("공간 정보를 수정한다.")
//    @Test
//    void UpdateSpace() {
//        //given
//        Space before = new Space(SpaceType.EDUCATION, "Art place", LocalTime.of(10, 0), LocalTime.of(16, 0), 10000, 15, 5, "또 오고 싶은 예술 공간", false, EnumSet.of(DetailedType.STUDY_ROOM), realEstate);
//
//        Space expectedSpace = new Space(SpaceType.ART, "Art place", LocalTime.of(10, 0), LocalTime.of(16, 0), 10000, 15, 5, "또 오고 싶은 예술 공간", false, EnumSet.of(DetailedType.DANCE_ROOM, DetailedType.VOCAL_ROOM), realEstate);
//        ResSpaces expected = mapper.SpaceToResponseSpace(expectedSpace);
//        RequestUpdateSpace req = new RequestUpdateSpace(SpaceType.ART, "Art place", LocalTime.of(10, 0), LocalTime.of(16, 0), 10000, 15, 5, "또 오고 싶은 예술 공간", EnumSet.of(DetailedType.DANCE_ROOM, DetailedType.VOCAL_ROOM));
//
//        when(spaceRepository.findByIdAndIsDeletedFalse(any())).thenReturn(Optional.of(before));
//        when(spaceRepository.save(any(Space.class))).thenReturn(expectedSpace);
//
//        //when
//        ResSpaces updatedSpace = spaceService.update(any(), req);
//
//        //then
//        assertThat(updatedSpace).isNotNull();
//        assertThat(updatedSpace).usingRecursiveComparison().isEqualTo(expected);
//    }
//
//    @DisplayName("공간 정보를 삭제한다.")
//    @Test
//    void DeleteSpace() {
//        //given
//        Space space = new Space(SpaceType.ART, "Art place", LocalTime.of(10, 0), LocalTime.of(16, 0), 10000, 15, 5, "또 오고 싶은 예술 공간", false, EnumSet.of(
//          DetailedType.DANCE_ROOM, DetailedType.VOCAL_ROOM), realEstate);
//
//        when(spaceRepository.findByIdAndIsDeletedFalse(any())).thenReturn(Optional.of(space));
//
//        //when
//        spaceService.delete(any());
//
//        //then
//        verify(spaceRepository).findByIdAndIsDeletedFalse(any());
//        assertThat(space.getIsDeleted()).isEqualTo(true);
//    }
//}

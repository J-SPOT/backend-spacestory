package com.juny.spacestory.integration;

import com.juny.spacestory.domain.DetailedType;
import com.juny.spacestory.domain.Space;
import com.juny.spacestory.domain.SpaceReservation;
import com.juny.spacestory.domain.SpaceType;
import com.juny.spacestory.repository.ReservationRepository;
import com.juny.spacestory.repository.SpaceRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@SpringBootTest
public class QueryTest {

    @Autowired
    ReservationRepository reservationRepository;

    @Autowired
    SpaceRepository spaceRepository;

    @DisplayName("기준에 따라 공간 찾는 쿼리 확인한다.")
    @Test
    void checkFindSpacesByCriteria() {
        PageRequest pageRequest = PageRequest.of(0, 4);
        Page<Space> byCriteriaQuerydsl = spaceRepository.findByCriteriaQuerydsl(SpaceType.FRIENDSHIP, "서울특별시", "강남구", 1, Set.of(DetailedType.CAFE, DetailedType.RESIDENCE),  pageRequest);
        System.out.println("byCriteriaQuerydsl.getTotalElements() = " + byCriteriaQuerydsl.getTotalElements()); // 세부사항 없이 24개, CAFE 9개, RESIDENCE 6개, PARTYROOM 9개

        /*
        SELECT s.*
FROM space s
JOIN real_estate re ON s.real_estate_id = re.id
JOIN space_detailed_type sdt ON s.id = sdt.space_id
wHERE s.space_type = "FRIENDSHIP" and re.sigungu = "강남구" and (sdt.detailed_types = "PARTY_ROOM" or sdt.detailed_types = "CAFE");
         */
    }
    @DisplayName("서울에 있는 공간 찾기 쿼리 확인한다.")
    @Test
    void checkQuerydsl() {
        Pageable pageRequest = PageRequest.of(0, 10);
        Page<Space> bySpaceTypeInSeoul = spaceRepository.findBySpaceTypeInSeoul(SpaceType.FRIENDSHIP, pageRequest);
        System.out.println("bySpaceTypeInSeoul.getTotalElements() = " + bySpaceTypeInSeoul.getTotalElements());

        Page<Space> bySpaceTypeInSeoulQuerydsl = spaceRepository.findBySpaceTypeInSeoulQuerydsl(SpaceType.FRIENDSHIP, pageRequest);
        System.out.println("bySpaceTypeInSeoulQuerydsl.getTotalElements() = " + bySpaceTypeInSeoulQuerydsl.getTotalElements());
    }
}

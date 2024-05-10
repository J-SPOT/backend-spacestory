package com.juny.spacestory.integration;

import com.juny.spacestory.domain.*;
import com.juny.spacestory.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
@SpringBootTest
public class MakeDummyData {

    @Autowired
    HostRepository hostRepository;

    @Autowired
    RealEstateRepository realEstateRepository;

    @Autowired
    SpaceRepository spaceRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ReservationRepository reservationRepository;

    private static final Map<SpaceType, Set<DetailedType>> validDetailedTypesMap = Map.of(
            SpaceType.FRIENDSHIP, EnumSet.of(DetailedType.PARTY_ROOM, DetailedType.RESIDENCE, DetailedType.CAFE),
            SpaceType.EVENT, EnumSet.of(DetailedType.PERFORMANCE_VENUE, DetailedType.CONFERENCE_HALL, DetailedType.EXHIBITION_HALL),
            SpaceType.EDUCATION, EnumSet.of(DetailedType.STUDY_ROOM, DetailedType.LECTURE_ROOM, DetailedType.SEMINAR_ROOM, DetailedType.MEETING_ROOM),
            SpaceType.ART, EnumSet.of(DetailedType.DANCE_ROOM, DetailedType.VOCAL_ROOM, DetailedType.INSTRUMENT_ROOM, DetailedType.DRAWING_ROOM, DetailedType.CRAFT_ROOM),
            SpaceType.SPORT, EnumSet.of(DetailedType.BADMINTON_COURT, DetailedType.FUTSAL_COURT, DetailedType.TENNIS_COURT),
            SpaceType.PHOTOGRAPHY, EnumSet.of(DetailedType.FILM_STUDIO, DetailedType.BROADCAST_ROOM) );

    private static final Map<String, List<String>> districtsAndDongs = new HashMap<>();

    private static SpaceType getRandomSpaceType () {
        Random random = new Random();
        SpaceType[] values = SpaceType.values();
        return values[random.nextInt(values.length)];
    }

    private static DetailedType getRandomDetailedType(Set<DetailedType> set) {
        int index = new Random().nextInt(set.size());
        Iterator<DetailedType> iter = set.iterator();
        for (int i = 0; i < index; i++) {
            iter.next();
        }
        return iter.next();
    }

    private static String getRandomDistrict(Set<String> districts) {
        Random random = new Random();
        List<String> districtList = new ArrayList<>(districts);
        return districtList.get(random.nextInt(districtList.size()));
    }

    private static String getRandomDong(List<String> list) {
        Random random = new Random();
        return list.get(random.nextInt(list.size()));
    }

    @Test
    @Transactional
    void HostAndRealEstateAndSpace() {
        districtsAndDongs.put("강남구", Arrays.asList("역삼동", "개포동", "청담동", "삼성동", "대치동", "신사동", "논현동", "압구정동", "세곡동", "자곡동", "율현동", "일원동", "수서동", "도곡동"));
        districtsAndDongs.put("강동구", Arrays.asList("명일동", "고덕동", "상일동", "길동", "둔춘동", "암사동", "성내동", "천호동", "강일동"));
        districtsAndDongs.put("강북구", Arrays.asList("미아동", "번동", "수유동", "우이동"));
        districtsAndDongs.put("강서구", Arrays.asList("염창동", "등촌동", "화곡동", "가양동", "마곡동", "내발산동", "외발산동", "공항동", "방화동"));
        districtsAndDongs.put("관악구", Arrays.asList("봉천동", "신림동", "남현동"));
        districtsAndDongs.put("광진구", Arrays.asList("중곡동", "능동", "구의동", "광장동", "자양동", "화양동", "군자동"));
        districtsAndDongs.put("구로구", Arrays.asList("신도림동", "구로동", "가리봉동", "고척동", "개봉동", "오류동", "궁동", "온수동", "천왕동", "항동"));
        districtsAndDongs.put("금천구", Arrays.asList("가산동", "독산동", "시흥동"));
        districtsAndDongs.put("노원구", Arrays.asList("월계동", "공릉동", "하계동", "상계동", "중계동"));
        districtsAndDongs.put("도봉구", Arrays.asList("쌍문동", "방학동", "창동", "도봉동"));
        districtsAndDongs.put("동대문구", Arrays.asList("신설동", "용두동", "제기동", "전농동", "답십리동", "장안동", "청량리동", "회기동", "휘경동", "이문동"));
        districtsAndDongs.put("동작구", Arrays.asList("노량진동", "상도동", "상도1동", "본동", "흑석동", "동작동", "사당동", "대방동", "신대방동"));
        districtsAndDongs.put("마포구", Arrays.asList("아현동", "공덕동", "신공덕동", "도화동", "용강동", "토정동", "마포동", "대흥동", "염리동", "노고산동", "신수동", "현석동", "구수동", "창전동", "상수동", "하중동", "신정동", "당인동", "서교동", "동교동", "합정동", "망원동", "연남동", "성산동", "중동", "상암동"));
        districtsAndDongs.put("서대문구", Arrays.asList("충정로2가", "충정로3가", "합동", "미근동", "냉천동", "천연동", "옥천동", "영천동", "현저동", "북아현동", "홍제동", "대현동", "대신동", "신촌동", "봉원동", "창천동", "연희동", "홍은동", "북가좌동", "남가좌동"));
        districtsAndDongs.put("서초구", Arrays.asList("방배동", "양재동", "우면동", "원지동", "잠원동", "반포동", "서초동", "내곡동", "염곡동", "신원동"));
        districtsAndDongs.put("성동구", Arrays.asList("상왕십리동", "하왕십리동", "홍익동", "도선동", "마장동", "사근동", "행당동", "응봉동", "금호동1가", "금호동2가", "금호동3가", "금호동4가", "옥수동", "성수동1가", "성수동2가", "송정동", "용답동"));
        districtsAndDongs.put("성북구", Arrays.asList("성북동", "성북동1가", "돈암동", "동소문동1가", "동소문동2가", "동소문동3가", "동소문동4가", "동소문동5가", "동소문동6가", "동소문동7가", "삼선동1가", "삼선동2가", "삼선동3가", "삼선동4가", "삼선동5가", "동선동1가", "동선동2가", "동선동3가", "동선동4가", "동선동5가", "안암동1가", "안암동2가", "안암동3가", "안암동4가", "안암동5가", "보문동4가", "보문동5가", "보문동6가", "보문동7가", "보문동1가", "보문동2가", "보문동3가", "정릉동", "길음동", "종암동", "하월곡동", "상월곡동", "장위동", "석관동"));
        districtsAndDongs.put("송파구", Arrays.asList("잠실동", "신천동", "풍납동", "송파동", "석촌동", "삼전동", "가락동", "문정동", "장지동", "방이동", "오금동", "거여동", "마천동"));
        districtsAndDongs.put("양천구", Arrays.asList("신정동", "목동", "신월동"));
        districtsAndDongs.put("영등포구", Arrays.asList("영등포동", "영등포동1가", "영등포동2가", "영등포동3가", "영등포동4가", "영등포동5가", "영등포동6가", "영등포동7가", "영등포동8가", "여의도동", "당산동1가", "당산동2가", "당산동3가", "당산동4가", "당산동5가", "당산동6가", "당산동", "도림동", "문래동1가", "문래동2가", "문래동3가", "문래동4가", "문래동5가", "문래동6가", "양평동1가", "양평동2가", "양평동3가", "양평동4가", "양평동5가", "양평동6가", "양화동", "신길동", "대림동", "양평동"));
        districtsAndDongs.put("용산구", Arrays.asList("후암동", "용산동2가", "용산동4가", "갈월동", "남영동", "용산동1가", "동자동", "서계동", "청파동1가", "청파동2가", "청파동3가", "원효로1가", "원효로2가", "신창동", "산천동", "청암동", "원효로3가", "원효로4가", "효창동", "도원동", "용문동", "문배동", "신계동", "한강로1가", "한강로2가", "용산동3가", "용산동5가", "한강로3가", "이촌동", "이태원동", "한남동", "동빙고동", "서빙고동", "주성동", "용산동6가", "보광동"));
        districtsAndDongs.put("은평구", Arrays.asList("수색동", "녹번동", "불광동", "갈현동", "구산동", "대조동", "응암동", "역촌동", "신사동", "증산동", "진관동"));
        districtsAndDongs.put("종로구", Arrays.asList("청운동", "신교동", "궁정동", "효자동", "창성동", "통의동", "적선동", "통인동", "누상동", "누하동", "옥인동", "체부동", "필운동", "내자동", "사직동", "도렴동", "당주동", "내수동", "세종로", "신문로1가", "신문로2가", "청진동", "서린동", "수송동", "중학동", "종로1가", "공평동", "관훈동", "견지동", "와룡동", "권농동", "운니동", "익선동", "경운동", "관철동", "인사동", "낙원동", "종로2가", "팔판동", "삼청동", "안국동", "소격동", "화동", "사간동", "송현동", "가회동", "재동", "계동", "원서동", "훈정동", "묘동", "봉익동", "돈의동", "장사동", "관수동", "종로3가", "인의동", "예지동", "원남동", "연지동", "종로4가", "효제동", "종로5가", "종로6가", "이화동", "연건동", "충신동", "동숭동", "혜화동", "명륜1가", "명륜2가", "명륜4가", "명륜3가", "창신동", "숭인동", "교남동", "평동", "송월동", "홍파동", "교북동", "행촌동", "구기동", "평창동", "부암동", "홍지동", "신영동", "무악동"));
        districtsAndDongs.put("중구", Arrays.asList("무교동", "다동", "태평로1가", "을지로1가", "을지로2가", "남대문로1가", "삼각동", "수하동", "장교동", "수표동", "소공동", "남창동", "북창동", "태평로2가", "남대문로2가", "남대문로3가", "남대문로4가", "남대문로5가", "봉래동1가", "봉래동2가", "회현동1가", "회현동2가", "회현동3가", "충무로1가", "충무로2가", "명동1가", "명동2가", "남산동1가", "남산동2가", "남산동3가", "저동1가", "충무로4가", "충무로5가", "인현동2가", "예관동", "묵정동", "필동1가", "필동2가", "필동3가", "남학동", "주자동", "예장동", "장충동1가", "장충동2가", "광희동1가", "광희동2가", "쌍림동", "을지로6가", "을지로7가", "을지로4가", "을지로5가", "주교동", "방산동", "오장동", "을지로3가", "입정동", "산림동", "충무로3가", "초동", "인현동1가", "저동2가", "신당동", "흥인동", "무학동", "황학동", "서소문동", "정동", "순화동", "의주로1가", "충정로1가", "중림동", "의주로2가", "만리동1가", "만리동2가"));
        districtsAndDongs.put("중랑구", Arrays.asList("면목동", "상봉동", "중화동", "묵동", "망우동", "신내동"));

        int st = 1;
        int n = 0;
        for (int i = st; i <= n; ++i) {
            String selectedDistrict = getRandomDistrict(districtsAndDongs.keySet());
            List<String> selectedDongs = districtsAndDongs.get(selectedDistrict);
            String selectedDong = getRandomDong(selectedDongs);
            SpaceType selectedSpaceType = getRandomSpaceType();
            Set<DetailedType> detailedTypes = validDetailedTypesMap.get(selectedSpaceType);
            DetailedType selectedDetailedType = getRandomDetailedType(detailedTypes);

            Random random = new Random();
			Host host = new Host("host" + i, 0L, false);
			hostRepository.save(host);
            Address address = new Address("도로명주소" + i, "저번주소" + i, "서울특별시", selectedDistrict, selectedDong);
            RealEstate realEstate = new RealEstate(address, random.nextInt(20) + 1, random.nextBoolean(), random.nextBoolean(), false, host);
            realEstateRepository.save(realEstate);
            Space space1 = new Space(selectedSpaceType, "spaceA" + i, LocalTime.of(random.nextInt(9) + 1, 0), LocalTime.of(random.nextInt(5) + 18, 0), (random.nextInt(5) + 1) * 10_000, random.nextInt(50) + 10, random.nextInt(20) + 1, "상세설명" + i, false, Set.of(selectedDetailedType), realEstate);
            Space space2 = new Space(selectedSpaceType, "spaceB" + i, LocalTime.of(random.nextInt(9) + 1, 0), LocalTime.of(random.nextInt(5) + 18, 0), (random.nextInt(5) + 1) * 10_000, random.nextInt(50) + 10, random.nextInt(20) + 1, "상세설명" + i + 1, false, Set.of(selectedDetailedType), realEstate);
            Space space3 = new Space(selectedSpaceType, "spaceC" + i, LocalTime.of(random.nextInt(9) + 1, 0), LocalTime.of(random.nextInt(5) + 18, 0), (random.nextInt(5) + 1) * 10_000, random.nextInt(50) + 10, random.nextInt(20) + 1, "상세설명" + i + 2, false, Set.of(selectedDetailedType), realEstate);
            spaceRepository.save(space1);
            spaceRepository.save(space2);
            spaceRepository.save(space3);

            User user = new User("user" + i, "user" + i + "@gmail.com", "nickname" + i, 300_000L, false);
            userRepository.save(user);
            LocalDate reservationDate = LocalDate.of(2024, 3, 3);
            LocalTime startTime = LocalTime.of(random.nextInt(3) + 9, 0);
            LocalTime endTime = LocalTime.of(random.nextInt(2) + 12, 0);
            long usageFee = Duration.between(startTime, endTime).toHours() * space1.getHourlyRate();
            SpaceReservation spaceReservation1 = new SpaceReservation(user.getId(), reservationDate, startTime, endTime, usageFee, true, false, space1);
            SpaceReservation spaceReservation2 = new SpaceReservation(user.getId(), reservationDate, startTime, endTime, usageFee, true, false, space2);
            SpaceReservation spaceReservation3 = new SpaceReservation(user.getId(), reservationDate, startTime, endTime, usageFee, true, false, space3);
            reservationRepository.save(spaceReservation1);
            reservationRepository.save(spaceReservation2);
            reservationRepository.save(spaceReservation3);
        }
    }
}

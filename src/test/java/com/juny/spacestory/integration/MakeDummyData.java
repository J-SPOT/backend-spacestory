package com.juny.spacestory.integration;

import com.juny.spacestory.reservation.entity.Reservation;
import com.juny.spacestory.space.domain.Space;
import com.juny.spacestory.space.domain.hashtag.Hashtag;
import com.juny.spacestory.space.domain.hashtag.HashtagRepository;
import com.juny.spacestory.space.domain.option.Option;
import com.juny.spacestory.space.domain.option.OptionRepository;
import com.juny.spacestory.space.domain.realestate.Address;
import com.juny.spacestory.space.domain.category.MainCategory;
import com.juny.spacestory.space.domain.realestate.RealEstate;
import com.juny.spacestory.space.domain.realestate.RealEstateRepository;
import com.juny.spacestory.reservation.repository.ReservationRepository;
import com.juny.spacestory.space.domain.category.SubCategory;
import com.juny.spacestory.space.domain.category.ResMainCategory;
import com.juny.spacestory.space.domain.category.ResSubCategory;
import com.juny.spacestory.space.domain.category.MainCategoryRepository;
import com.juny.spacestory.space.repository.SpaceRepository;
import com.juny.spacestory.space.domain.category.SubCategoryRepository;
import com.juny.spacestory.space.domain.category.CategoryService;
import com.juny.spacestory.user.domain.User;
import com.juny.spacestory.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.time.Duration;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;

@SpringBootTest
public class MakeDummyData {

  private final Map<String, List<String>> districtsAndDongs = new HashMap<>();
  @Autowired
  PasswordEncoder passwordEncoder;
  @Autowired
  RealEstateRepository realEstateRepository;
  @Autowired
  SpaceRepository spaceRepository;
  @Autowired
  UserRepository userRepository;
  @Autowired
  ReservationRepository reservationRepository;
  @Autowired
  CategoryService categoryService;
  @Autowired
  MainCategoryRepository mainCategoryRepository;
  @Autowired
  SubCategoryRepository subCategoryRepository;
  @Autowired
  OptionRepository optionRepository;
  @Autowired
  private HashtagRepository hashtagRepository;

  private MainCategory getRandomMainCategory() {
    Random random = new Random();
    List<ResMainCategory> mainCategories = categoryService.findMainCategories();

    Long mainCategoryId = mainCategories.get(random.nextInt(mainCategories.size())).id();

    return mainCategoryRepository.findById(mainCategoryId).orElseThrow(
      () -> new RuntimeException());
  }

  private SubCategory getRandomSubCategory(Long id) {
    Random random = new Random();
    List<ResSubCategory> subCategories = categoryService.findSubCategoriesByMainCategoryId(id);

    Long subCategoryId = subCategories.get(random.nextInt(subCategories.size())).id();

    return subCategoryRepository.findById(subCategoryId).orElseThrow(
      () -> new RuntimeException());
  }

  private String getRandomDistrict(Set<String> districts) {
    Random random = new Random();
    List<String> districtList = new ArrayList<>(districts);
    return districtList.get(random.nextInt(districtList.size()));
  }

  private String getRandomDong(List<String> list) {
    Random random = new Random();
    return list.get(random.nextInt(list.size()));
  }

//  @Test
//  @Transactional
//  @Rollback(false)
  void HostAndRealEstateAndSpace() {

    int st = 101;
    int n = 300;

    districtsAndDongs.put("강남구",
      Arrays.asList("역삼동", "개포동", "청담동", "삼성동", "대치동", "신사동", "논현동", "압구정동", "세곡동", "자곡동", "율현동",
        "일원동", "수서동", "도곡동"));
    districtsAndDongs.put("강동구",
      Arrays.asList("명일동", "고덕동", "상일동", "길동", "둔춘동", "암사동", "성내동", "천호동", "강일동"));
    districtsAndDongs.put("강북구", Arrays.asList("미아동", "번동", "수유동", "우이동"));
    districtsAndDongs.put("강서구",
      Arrays.asList("염창동", "등촌동", "화곡동", "가양동", "마곡동", "내발산동", "외발산동", "공항동", "방화동"));
    districtsAndDongs.put("관악구", Arrays.asList("봉천동", "신림동", "남현동"));
    districtsAndDongs.put("광진구", Arrays.asList("중곡동", "능동", "구의동", "광장동", "자양동", "화양동", "군자동"));
    districtsAndDongs.put("구로구",
      Arrays.asList("신도림동", "구로동", "가리봉동", "고척동", "개봉동", "오류동", "궁동", "온수동", "천왕동", "항동"));
    districtsAndDongs.put("금천구", Arrays.asList("가산동", "독산동", "시흥동"));
    districtsAndDongs.put("노원구", Arrays.asList("월계동", "공릉동", "하계동", "상계동", "중계동"));
    districtsAndDongs.put("도봉구", Arrays.asList("쌍문동", "방학동", "창동", "도봉동"));
    districtsAndDongs.put("동대문구",
      Arrays.asList("신설동", "용두동", "제기동", "전농동", "답십리동", "장안동", "청량리동", "회기동", "휘경동", "이문동"));
    districtsAndDongs.put("동작구",
      Arrays.asList("노량진동", "상도동", "상도1동", "본동", "흑석동", "동작동", "사당동", "대방동", "신대방동"));
    districtsAndDongs.put("마포구",
      Arrays.asList("아현동", "공덕동", "신공덕동", "도화동", "용강동", "토정동", "마포동", "대흥동", "염리동", "노고산동", "신수동",
        "현석동", "구수동", "창전동", "상수동", "하중동", "신정동", "당인동", "서교동", "동교동", "합정동", "망원동", "연남동", "성산동",
        "중동", "상암동"));
    districtsAndDongs.put("서대문구",
      Arrays.asList("충정로2가", "충정로3가", "합동", "미근동", "냉천동", "천연동", "옥천동", "영천동", "현저동", "북아현동", "홍제동",
        "대현동", "대신동", "신촌동", "봉원동", "창천동", "연희동", "홍은동", "북가좌동", "남가좌동"));
    districtsAndDongs.put("서초구",
      Arrays.asList("방배동", "양재동", "우면동", "원지동", "잠원동", "반포동", "서초동", "내곡동", "염곡동", "신원동"));
    districtsAndDongs.put("성동구",
      Arrays.asList("상왕십리동", "하왕십리동", "홍익동", "도선동", "마장동", "사근동", "행당동", "응봉동", "금호동1가", "금호동2가",
        "금호동3가", "금호동4가", "옥수동", "성수동1가", "성수동2가", "송정동", "용답동"));
    districtsAndDongs.put("성북구",
      Arrays.asList("성북동", "성북동1가", "돈암동", "동소문동1가", "동소문동2가", "동소문동3가", "동소문동4가", "동소문동5가",
        "동소문동6가", "동소문동7가", "삼선동1가", "삼선동2가", "삼선동3가", "삼선동4가", "삼선동5가", "동선동1가", "동선동2가", "동선동3가",
        "동선동4가", "동선동5가", "안암동1가", "안암동2가", "안암동3가", "안암동4가", "안암동5가", "보문동4가", "보문동5가", "보문동6가",
        "보문동7가", "보문동1가", "보문동2가", "보문동3가", "정릉동", "길음동", "종암동", "하월곡동", "상월곡동", "장위동", "석관동"));
    districtsAndDongs.put("송파구",
      Arrays.asList("잠실동", "신천동", "풍납동", "송파동", "석촌동", "삼전동", "가락동", "문정동", "장지동", "방이동", "오금동",
        "거여동", "마천동"));
    districtsAndDongs.put("양천구", Arrays.asList("신정동", "목동", "신월동"));
    districtsAndDongs.put("영등포구",
      Arrays.asList("영등포동", "영등포동1가", "영등포동2가", "영등포동3가", "영등포동4가", "영등포동5가", "영등포동6가", "영등포동7가",
        "영등포동8가", "여의도동", "당산동1가", "당산동2가", "당산동3가", "당산동4가", "당산동5가", "당산동6가", "당산동", "도림동",
        "문래동1가", "문래동2가", "문래동3가", "문래동4가", "문래동5가", "문래동6가", "양평동1가", "양평동2가", "양평동3가", "양평동4가",
        "양평동5가", "양평동6가", "양화동", "신길동", "대림동", "양평동"));
    districtsAndDongs.put("용산구",
      Arrays.asList("후암동", "용산동2가", "용산동4가", "갈월동", "남영동", "용산동1가", "동자동", "서계동", "청파동1가", "청파동2가",
        "청파동3가", "원효로1가", "원효로2가", "신창동", "산천동", "청암동", "원효로3가", "원효로4가", "효창동", "도원동", "용문동",
        "문배동", "신계동", "한강로1가", "한강로2가", "용산동3가", "용산동5가", "한강로3가", "이촌동", "이태원동", "한남동", "동빙고동",
        "서빙고동", "주성동", "용산동6가", "보광동"));
    districtsAndDongs.put("은평구",
      Arrays.asList("수색동", "녹번동", "불광동", "갈현동", "구산동", "대조동", "응암동", "역촌동", "신사동", "증산동", "진관동"));
    districtsAndDongs.put("종로구",
      Arrays.asList("청운동", "신교동", "궁정동", "효자동", "창성동", "통의동", "적선동", "통인동", "누상동", "누하동", "옥인동",
        "체부동", "필운동", "내자동", "사직동", "도렴동", "당주동", "내수동", "세종로", "신문로1가", "신문로2가", "청진동", "서린동",
        "수송동", "중학동", "종로1가", "공평동", "관훈동", "견지동", "와룡동", "권농동", "운니동", "익선동", "경운동", "관철동", "인사동",
        "낙원동", "종로2가", "팔판동", "삼청동", "안국동", "소격동", "화동", "사간동", "송현동", "가회동", "재동", "계동", "원서동",
        "훈정동", "묘동", "봉익동", "돈의동", "장사동", "관수동", "종로3가", "인의동", "예지동", "원남동", "연지동", "종로4가", "효제동",
        "종로5가", "종로6가", "이화동", "연건동", "충신동", "동숭동", "혜화동", "명륜1가", "명륜2가", "명륜4가", "명륜3가", "창신동",
        "숭인동", "교남동", "평동", "송월동", "홍파동", "교북동", "행촌동", "구기동", "평창동", "부암동", "홍지동", "신영동", "무악동"));
    districtsAndDongs.put("중구",
      Arrays.asList("무교동", "다동", "태평로1가", "을지로1가", "을지로2가", "남대문로1가", "삼각동", "수하동", "장교동", "수표동",
        "소공동", "남창동", "북창동", "태평로2가", "남대문로2가", "남대문로3가", "남대문로4가", "남대문로5가", "봉래동1가", "봉래동2가",
        "회현동1가", "회현동2가", "회현동3가", "충무로1가", "충무로2가", "명동1가", "명동2가", "남산동1가", "남산동2가", "남산동3가",
        "저동1가", "충무로4가", "충무로5가", "인현동2가", "예관동", "묵정동", "필동1가", "필동2가", "필동3가", "남학동", "주자동",
        "예장동", "장충동1가", "장충동2가", "광희동1가", "광희동2가", "쌍림동", "을지로6가", "을지로7가", "을지로4가", "을지로5가", "주교동",
        "방산동", "오장동", "을지로3가", "입정동", "산림동", "충무로3가", "초동", "인현동1가", "저동2가", "신당동", "흥인동", "무학동",
        "황학동", "서소문동", "정동", "순화동", "의주로1가", "충정로1가", "중림동", "의주로2가", "만리동1가", "만리동2가"));
    districtsAndDongs.put("중랑구", Arrays.asList("면목동", "상봉동", "중화동", "묵동", "망우동", "신내동"));

    Map<String, String> spaceDescriptions = new HashMap<>();

    // 공간 소개 추가
    spaceDescriptions.put("파티룸", "최고의 파티를 꿈꾸신다면, 저희 파티룸을 선택하세요! 최신 음향과 조명 시스템을 갖춘 넓은 공간에서 여러분의 특별한 순간을 더욱 빛나게 만들어 드립니다. 테마와 장식도 여러분의 취향에 맞춰 꾸밀 수 있어, 맞춤형 파티가 가능합니다. 게다가 케이터링 서비스도 제공하여 음식 준비에 대한 걱정 없이 파티를 즐길 수 있습니다. 저희 파티룸은 잊지 못할 추억을 만들어 드리는 완벽한 장소입니다.");
    spaceDescriptions.put("스터디룸", "집중력 있는 학습 공간을 찾고 계신가요? 저희 스터디룸은 조용하고 쾌적한 환경을 제공하여 최상의 학습 효과를 보장합니다. 넓은 책상, 편안한 의자, 고속 인터넷, 화이트보드, 프로젝터 등 학습에 필요한 모든 도구가 구비되어 있습니다. 개인 학습뿐만 아니라 그룹 스터디에도 최적화된 공간에서 목표를 달성해 보세요. 성공적인 학습을 위한 최고의 선택입니다.");
    spaceDescriptions.put("공유주방", "요리를 사랑하는 여러분을 위한 최고의 공간, 저희 공유주방입니다. 최신 주방 기기와 넓은 작업 공간에서 다양한 요리를 시도해 보세요. 요리 교실, 쿠킹 파티 등 다양한 이벤트를 개최할 수 있는 완벽한 구조를 자랑합니다. 주방 용품과 식재료가 잘 정리되어 있어 사용이 편리하며, 쾌적한 환경에서 요리의 즐거움을 만끽할 수 있습니다. 요리를 통해 새로운 사람들과 교류하고 다양한 요리 기술을 배울 수 있는 공간입니다.");
    spaceDescriptions.put("회의실", "저희 회의실은 비즈니스 성공을 위한 최적의 공간입니다. 최신 AV 장비와 편안한 가구가 구비되어 있어 생산적인 회의를 진행할 수 있습니다. 고속 인터넷과 화상 회의 시스템도 제공되어 원격 회의에도 최적화되어 있습니다. 넓고 쾌적한 공간에서 중요한 결정을 내리고 팀과 협업할 수 있습니다. 저희 회의실은 집중력과 창의력을 높이는 환경을 제공하여 성공적인 비즈니스 회의를 보장합니다.");
    spaceDescriptions.put("카페", "분위기 좋은 카페에서 휴식과 사교를 즐기세요. 저희 카페는 다양한 커피와 디저트를 제공하며, 아늑한 분위기에서 친구들과의 만남이나 조용한 시간을 보낼 수 있습니다. 고급스러운 인테리어와 편안한 좌석이 마련되어 있어 하루의 피로를 풀기에 좋습니다. 무료 Wi-Fi가 제공되어 간단한 업무나 스터디 모임에도 적합합니다. 일상에서 잠시 벗어나 여유를 즐길 수 있는 완벽한 장소입니다.");
    spaceDescriptions.put("세미나실", "전문적인 세미나와 교육을 위한 공간을 찾고 계신가요? 저희 세미나실은 최대 50명을 수용할 수 있는 넓은 공간과 최신 교육 장비를 갖추고 있습니다. 프로젝터, 화이트보드, 음향 시스템 등이 구비되어 있어 원활한 발표와 토론이 가능합니다. 쾌적한 환경과 전문적인 시설을 제공하여 모든 참석자들이 집중할 수 있는 학습 환경을 조성합니다. 성공적인 세미나와 교육을 위한 최적의 선택입니다.");
    spaceDescriptions.put("강의실", "저희 강의실은 다양한 교육 프로그램을 위한 최고의 공간입니다. 넓고 쾌적한 환경에서 효과적인 학습이 가능하며, 최신 교육 장비가 제공되어 강의, 세미나, 워크샵 등 다양한 활동에 적합합니다. 조용하고 집중할 수 있는 분위기를 제공하여 모든 학습자가 최상의 결과를 얻을 수 있도록 지원합니다. 최고의 교육 경험을 제공하는 저희 강의실을 이용해 보세요.");

    spaceDescriptions.put("연습실", "다양한 예술 활동을 위한 최고의 연습 공간, 저희 연습실입니다. 넓고 쾌적한 환경에서 자유롭게 연습할 수 있으며, 최신 장비와 방음 시설이 구비되어 있어 집중력 있는 연습이 가능합니다. 개인 또는 그룹 연습에 모두 적합한 공간에서 창작과 예술 활동을 지원합니다. 최고의 연습 환경을 제공하는 저희 연습실에서 최고의 결과를 만들어보세요!");
    spaceDescriptions.put("댄스연습실", "댄서들을 위한 최적의 공간, 저희 댄스연습실입니다. 넓은 거울과 고급 마루 바닥이 설치되어 있으며, 음향 시스템이 구비되어 있어 다양한 춤을 연습할 수 있습니다. 개인 연습부터 그룹 레슨까지 모두 가능한 구조로 되어 있습니다. 쾌적한 공기와 밝은 조명이 연습 효율을 높여주며, 자유롭게 움직일 수 있는 넓은 공간이 제공됩니다. 최고의 춤 실력을 발휘할 수 있는 저희 댄스연습실에서 연습하세요!");
    spaceDescriptions.put("보컬연습실", "가수와 성악가들을 위한 최고의 공간, 저희 보컬연습실입니다. 고급 음향 장비와 방음 시설이 완비되어 있어, 소음 걱정 없이 마음껏 노래를 연습할 수 있습니다. 개인 레슨이나 그룹 연습에 모두 적합하며, 편안한 환경에서 목소리 훈련을 할 수 있습니다. 모든 보컬 연습에 필요한 최적의 조건을 갖추고 있어, 가창력을 극대화할 수 있는 공간입니다. 최고의 노래 실력을 발휘할 수 있는 저희 보컬연습실에서 연습하세요!");
    spaceDescriptions.put("악기연습실", "음악가들을 위한 전문 연습 공간, 저희 악기연습실입니다. 피아노, 기타, 드럼 등 다양한 악기 연습에 적합한 시설이 구비되어 있으며, 방음 시설이 잘 되어 있어 다른 사람에게 방해되지 않고 연습할 수 있습니다. 개인 연습과 그룹 연습 모두 가능하며, 쾌적한 환경에서 집중력 있는 연습이 가능합니다. 음악가들에게 최상의 연습 환경을 제공합니다. 최고의 연주 실력을 발휘할 수 있는 저희 악기연습실에서 연습하세요!");
    spaceDescriptions.put("운동시설", "다양한 체육 활동을 위한 최적의 공간, 저희 운동시설입니다. 최신 운동 기구와 넓은 운동장이 구비되어 있어 다양한 운동을 즐길 수 있습니다. 개인 트레이닝부터 그룹 운동까지 모두 가능한 구조로 되어 있으며, 쾌적한 환경에서 운동의 즐거움을 만끽할 수 있습니다. 건강과 체력을 키우기 위한 최적의 장소입니다. 최고의 운동 경험을 제공하는 저희 운동시설에서 건강한 몸을 만들어 보세요!");
    spaceDescriptions.put("녹음실", "음악 제작과 음향 작업을 위한 전문 공간, 저희 녹음실입니다. 최신 녹음 장비와 방음 시설이 완비되어 있어 고품질의 녹음이 가능합니다. 음악가, 성우, 팟캐스터 등 다양한 음향 작업을 하는 이들에게 최적의 조건을 제공합니다. 창작과 프로덕션을 위한 완벽한 환경을 갖추고 있어, 최고의 결과물을 만들어낼 수 있는 공간입니다. 최고의 녹음 품질을 보장하는 저희 녹음실을 이용해 보세요!");

    spaceDescriptions.put("촬영스튜디오", "사진과 영상 촬영을 위한 최고의 공간, 저희 촬영스튜디오입니다. 다양한 배경과 조명 장비가 갖추어져 있어 창의적인 촬영이 가능합니다. 넓은 공간에서 자유롭게 촬영할 수 있으며, 프로페셔널한 결과물을 만들어낼 수 있습니다. 광고, 프로필, 제품 사진 등 다양한 촬영에 적합한 장소입니다. 최고의 촬영 환경을 제공하는 저희 촬영스튜디오에서 멋진 작품을 만들어 보세요!");
    spaceDescriptions.put("렌탈스튜디오", "다양한 목적의 촬영을 위한 대여 공간, 저희 렌탈스튜디오입니다. 최신 촬영 장비와 다양한 배경이 구비되어 있어, 원하는 스타일의 촬영이 가능합니다. 넓은 공간에서 자유롭게 촬영할 수 있으며, 필요한 장비와 소품을 편리하게 이용할 수 있습니다. 프로페셔널한 촬영 환경을 제공합니다. 최고의 촬영 경험을 제공하는 저희 렌탈스튜디오에서 멋진 작품을 만들어 보세요!");
    spaceDescriptions.put("라이브방송", "실시간 방송을 위한 최적의 공간, 저희 라이브방송 스튜디오입니다. 최신 방송 장비와 고속 인터넷이 구비되어 있어, 안정적인 스트리밍이 가능합니다. 유튜브, 트위치 등 다양한 플랫폼에서 라이브 방송을 진행할 수 있으며, 쾌적한 환경에서 프로페셔널한 방송을 제작할 수 있습니다. 창의적인 방송 콘텐츠를 위한 완벽한 공간입니다. 최고의 라이브 방송 경험을 제공하는 저희 스튜디오를 이용해 보세요!");
    spaceDescriptions.put("호리존", "다양한 촬영을 위한 전용 공간, 저희 호리존 스튜디오입니다. 대형 배경과 고급 조명 장비가 구비되어 있어, 다양한 스타일의 촬영이 가능합니다. 넓은 공간에서 자유롭게 촬영할 수 있으며, 전문적인 결과물을 만들어낼 수 있습니다. 광고, 프로필, 제품 사진 등 다양한 촬영에 적합한 장소입니다. 최고의 촬영 환경을 제공하는 저희 호리존 스튜디오에서 멋진 작품을 만들어 보세요!");
    spaceDescriptions.put("실외촬영", "자연광과 아름다운 배경을 활용한 촬영을 위한 공간, 저희 실외촬영 장소입니다. 다양한 실외 장소에서 촬영할 수 있으며, 자연스러운 분위기의 사진과 영상을 만들어낼 수 있습니다. 특별한 날이나 이벤트, 광고 촬영에 적합한 환경을 제공합니다. 최고의 실외 촬영 경험을 제공하는 저희 촬영 장소에서 멋진 작품을 만들어 보세요!");
    spaceDescriptions.put("가정집", "집 안에서의 자연스러운 분위기를 연출할 수 있는 공간, 저희 가정집 촬영 장소입니다. 다양한 인테리어와 생활 소품이 구비되어 있어, 홈 스타일의 촬영에 적합합니다. 가정집 촬영은 광고, 드라마, 사진 촬영 등 다양한 목적에 맞게 활용할 수 있습니다. 최고의 가정집 촬영 경험을 제공하는 저희 장소에서 멋진 작품을 만들어 보세요!");

    spaceDescriptions.put("공연장", "다양한 공연과 이벤트를 위한 최고의 공간, 저희 공연장입니다. 최신 음향 및 조명 시스템이 갖추어져 있어, 다양한 공연을 성공적으로 개최할 수 있습니다. 넓은 무대와 관객석이 마련되어 있어, 많은 사람들을 수용할 수 있으며, 프로페셔널한 공연 환경을 제공합니다. 음악, 연극, 무용 등 다양한 공연에 적합한 장소입니다. 최고의 공연 경험을 제공하는 저희 공연장에서 멋진 무대를 만들어 보세요!");
    spaceDescriptions.put("컨퍼런스", "비즈니스 회의와 세미나를 위한 최적의 공간, 저희 컨퍼런스 룸입니다. 최신 AV 장비와 편안한 가구가 구비되어 있어, 생산적인 회의를 진행할 수 있습니다. 넓고 쾌적한 공간에서 중요한 결정을 내리고 팀과 협업할 수 있습니다. 집중력과 창의력을 높이는 환경을 제공하여 성공적인 비즈니스 회의를 보장합니다. 저희 컨퍼런스 룸에서 중요한 결정을 내리고 비즈니스 성과를 극대화해 보세요!");
    spaceDescriptions.put("스몰웨딩", "소규모 결혼식을 위한 아름다운 장소, 저희 스몰웨딩 공간입니다. 고급스럽고 아늑한 분위기에서 소중한 사람들과 함께하는 특별한 결혼식을 만들 수 있습니다. 맞춤형 웨딩 서비스를 제공하여 신랑 신부의 꿈을 현실로 만들어줍니다. 친밀하고 따뜻한 결혼식을 원하는 커플에게 이상적인 선택입니다. 최고의 결혼식을 만들어드리는 저희 스몰웨딩 공간을 선택해 주세요!");
    spaceDescriptions.put("갤러리", "예술 작품을 전시하고 감상할 수 있는 최고의 공간, 저희 갤러리입니다. 다양한 예술 작품이 전시되어 있으며, 쾌적한 환경에서 작품을 감상할 수 있습니다. 갤러리는 아티스트와 예술 애호가들이 교류할 수 있는 장소로, 다양한 전시회와 이벤트가 열립니다. 예술과 문화를 즐길 수 있는 최적의 공간입니다. 최고의 예술 전시 경험을 제공하는 저희 갤러리를 방문해 보세요!");

    spaceDescriptions.put("독립오피스", "개인 또는 소규모 팀을 위한 전용 사무 공간, 저희 독립오피스입니다. 편안하고 조용한 환경에서 집중적으로 업무를 진행할 수 있습니다. 최신 사무 기기와 고속 인터넷이 제공되어 생산성을 극대화할 수 있습니다. 비즈니스 미팅, 프로젝트 작업 등 다양한 업무 활동에 적합한 공간입니다. 최고의 업무 환경을 제공하는 저희 독립오피스를 선택해 보세요!");
    spaceDescriptions.put("코워킹오피스", "다양한 분야의 사람들이 함께 일할 수 있는 공유 사무 공간, 저희 코워킹오피스입니다. 협업과 네트워킹을 위한 최적의 환경을 제공하며, 편안한 업무 공간과 다양한 편의시설이 구비되어 있습니다. 창의적인 아이디어와 협력을 통해 비즈니스 성장을 도모할 수 있는 장소입니다. 최고의 업무 환경과 네트워킹 기회를 제공하는 저희 코워킹오피스를 이용해 보세요!");

    Map<String, String[]> hashtagMap = new HashMap<>();
    hashtagMap.put("파티룸", new String[]{"파티", "생일파티", "모임", "축하", "이벤트", "즐거움", "친구", "축제", "기념일", "파티장소"});
    hashtagMap.put("스터디룸", new String[]{"공부", "조용한", "집중", "회의", "그룹스터디", "독서실", "학습", "학업", "공부방", "스터디카페"});
    hashtagMap.put("공유주방", new String[]{"요리", "주방", "공유", "레시피", "쿠킹", "쿠킹클래스", "요리공간", "셰프", "맛집", "음식"});
    hashtagMap.put("회의실", new String[]{"회의", "비즈니스", "프레젠테이션", "팀미팅", "협업", "사무실", "업무", "미팅룸", "프로젝트", "컨퍼런스룸"});
    hashtagMap.put("카페", new String[]{"커피", "디저트", "휴식", "카페모임", "분위기", "커피숍", "음료", "카페인테리어", "브런치", "카페추천"});
    hashtagMap.put("세미나실", new String[]{"세미나", "워크샵", "강연", "프레젠테이션", "교육", "토론", "세미나룸", "강의", "학습", "스피치"});
    hashtagMap.put("강의실", new String[]{"강의", "수업", "교육", "학습", "교실", "강좌", "학원", "학습공간", "교육시설", "교습소"});
    hashtagMap.put("연습실", new String[]{"연습", "자유연습", "트레이닝", "개인연습", "연습공간", "체력단련", "스포츠", "운동", "피트니스", "연습장"});
    hashtagMap.put("댄스연습실", new String[]{"댄스", "춤연습", "안무", "무용", "댄스트레이닝", "댄스스튜디오", "공연연습", "댄스클래스", "춤수업", "댄스배우기"});
    hashtagMap.put("보컬연습실", new String[]{"보컬", "노래연습", "가창", "성악", "보컬트레이닝", "노래수업", "보컬학원", "음악연습", "보컬수업", "발성연습"});
    hashtagMap.put("악기연습실", new String[]{"악기", "연주", "연습", "음악", "악기트레이닝", "피아노연습", "기타연습", "음악공간", "악기배우기", "음악수업"});
    hashtagMap.put("운동시설", new String[]{"운동", "헬스", "피트니스", "체육", "스포츠", "체력단련", "헬스장", "운동장", "운동수업", "체육시설"});
    hashtagMap.put("녹음실", new String[]{"녹음", "스튜디오", "레코딩", "음악", "프로듀싱", "음향", "녹음장비", "녹음기술", "보컬녹음", "사운드"});
    hashtagMap.put("촬영스튜디오", new String[]{"촬영", "사진", "스튜디오", "포토그래피", "촬영장", "사진촬영", "포트레이트", "인물사진", "스튜디오대여", "사진관"});
    hashtagMap.put("렌탈스튜디오", new String[]{"렌탈", "대여", "스튜디오", "공간대여", "임대", "촬영공간", "스튜디오임대", "렌탈서비스", "촬영장소", "스튜디오렌탈"});
    hashtagMap.put("라이브방송", new String[]{"라이브", "방송", "스트리밍", "유튜브", "실시간", "라이브스트리밍", "방송장비", "인터넷방송", "방송제작", "실시간방송"});
    hashtagMap.put("호리존", new String[]{"호리존", "촬영", "배경", "무대", "프로덕션", "촬영배경", "무대제작", "프로덕션서비스", "촬영스튜디오", "배경설치"});
    hashtagMap.put("실외촬영", new String[]{"실외", "야외촬영", "로케이션", "사진", "영상", "야외사진", "촬영지", "로케이션촬영", "야외장소", "촬영스팟"});
    hashtagMap.put("가정집", new String[]{"가정집", "홈", "하우스", "촬영", "생활", "집촬영", "실내촬영", "홈스튜디오", "가정촬영", "하우스촬영"});
    hashtagMap.put("공연장", new String[]{"공연", "무대", "라이브", "콘서트", "이벤트", "공연장소", "공연기획", "라이브공연", "콘서트홀", "이벤트장소"});
    hashtagMap.put("컨퍼런스", new String[]{"컨퍼런스", "회의", "세미나", "비즈니스", "프레젠테이션", "컨퍼런스룸", "회의실", "비즈니스회의", "워크샵", "세미나룸"});
    hashtagMap.put("스몰웨딩", new String[]{"웨딩", "결혼식", "스몰웨딩", "이벤트", "축하", "웨딩플래너", "웨딩촬영", "결혼준비", "스몰웨딩장소", "웨딩홀"});
    hashtagMap.put("갤러리", new String[]{"갤러리", "전시", "예술", "아트", "전시회", "미술관", "아트갤러리", "예술작품", "전시공간", "예술전시"});
    hashtagMap.put("독립오피스", new String[]{"오피스", "사무실", "독립", "업무", "비즈니스", "독립사무실", "업무공간", "사무실임대", "비즈니스공간", "개인사무실"});
    hashtagMap.put("코워킹오피스", new String[]{"코워킹", "공유오피스", "협업", "비즈니스", "네트워킹", "코워킹스페이스", "협업공간", "공유사무실", "비즈니스센터", "네트워킹공간"});

    String reservationNote = """
      이용당일(첫 날) 이후에 환불 관련 사항은 호스트에게 직접 문의하셔야 합니다.
      결제 후 2시간 이내에는 100% 환불이 가능합니다. (단, 이용시간 전까지만 가능)
      이용 8일 전: 총 금액의 100% 환불
      이용 7일 전: 총 금액의 90% 환불
      이용 6일 전: 총 금액의 80% 환불
      이용 5일 전: 총 금액의 70% 환불
      이용 4일 전: 총 금액의 60% 환불
      이용 3일 전: 총 금액의 50% 환불
      이용 2일 전: 환불 불가
      이용 전날: 환불 불가
      이용 당일: 환불 불가""";

    String[] namePrefixes = {
      "cool", "fun", "smart", "happy", "bright", "dark", "crazy", "wild", "mystic", "fast",
      "strong", "lazy", "clever", "gentle", "kind", "brave", "bold", "sweet", "fancy", "chill",
      "fierce", "calm", "silent", "loud", "quick", "calm", "shiny", "glow", "storm", "light",
      "shadow", "fire", "water", "earth", "wind", "sky", "moon", "star", "sun", "cloud",
      "rain", "snow", "frost", "ice", "flame", "ember", "breeze", "thunder", "blaze", "mist",
      "rose", "lily", "daisy", "jasmine", "pearl", "ruby", "opal", "topaz", "amber", "onyx",
      "jet", "zinc", "silver", "gold", "platinum", "iron", "steel", "bronze", "copper", "tin",
      "lead", "quartz", "crystal", "diamond", "garnet", "sapphire", "emerald", "jade", "ivory",
      "ebony", "maple", "oak", "pine", "willow", "ash", "birch", "cedar", "fir", "spruce",
      "palm", "cactus", "vine", "bamboo", "fern", "moss", "ivy", "reed", "flora", "fauna"
    };

    String[] nameSuffixes = {
      "cat", "dog", "tiger", "lion", "bear", "fox", "wolf", "eagle", "hawk", "owl",
      "fish", "shark", "whale", "dolphin", "dragon", "phoenix", "unicorn", "griffin", "sprite", "elf",
      "gnome", "dwarf", "giant", "orc", "troll", "goblin", "mermaid", "fairy", "centaur", "minotaur",
      "golem", "vampire", "werewolf", "witch", "wizard", "mage", "knight", "samurai", "ninja", "pirate",
      "alien", "robot", "cyborg", "zombie", "ghost", "skeleton", "mummy", "specter", "shadow", "wraith",
      "golem", "spirit", "elemental", "djinn", "sphinx", "kraken", "leviathan", "hydra", "behemoth", "chimera",
      "griffon", "basilisk", "cockatrice", "wyvern", "pegasus", "hippogriff", "drake", "wyrm", "lizard", "serpent",
      "frog", "toad", "newt", "salamander", "gecko", "chameleon", "iguana", "turtle", "tortoise", "dino",
      "raptor", "rex", "brachio", "trike", "pterosaur", "sauropod", "theropod", "stego", "ankylo", "carno"
    };

    String[] emails = {
      "gmail.com", "naver.com", "daum.net", "hotmail.com", "yahoo.com", "outlook.com", "hanmail.net", "nate.com",
      "kakao.com", "icloud.com", "mail.com", "live.com", "inbox.com", "gmx.com", "lycos.com", "zoho.com",
      "protonmail.com", "tutanota.com", "aol.com", "fastmail.com"
    };


    for (int i = st; i <= n; ++i) {
      Random random = new Random();

      // Host
      String name = namePrefixes[random.nextInt(namePrefixes.length)] + nameSuffixes[random.nextInt(nameSuffixes.length)];
      User host = new User(name, name + i + "h" + "@" + emails[random.nextInt(emails.length)], passwordEncoder.encode("1234"),
        "172.21.0.3");
      host.getIpAddresses().add("220.93.17.226");
      host.getIpAddresses().add("127.0.0.1");
      host.payFeeForHost(-100_000);
      userRepository.save(host);

      // RealEstate
      String selectedDistrict = getRandomDistrict(districtsAndDongs.keySet());
      List<String> selectedDongs = districtsAndDongs.get(selectedDistrict);
      String selectedDong = getRandomDong(selectedDongs);

      Address address = new Address("도로명주소" + i, "저번주소" + i, "서울특별시", selectedDistrict,
        selectedDong);
      RealEstate realEstate = new RealEstate(address, random.nextInt(20) + 1, random.nextBoolean(),
        random.nextBoolean());

      // RealEstate - Host
      realEstate.setHost(host);
      realEstateRepository.save(realEstate);

      // Category
      MainCategory randomMainCategory = getRandomMainCategory();
      mainCategoryRepository.save(randomMainCategory);
      SubCategory randomSubCategory1 = getRandomSubCategory(randomMainCategory.getId());
      SubCategory randomSubCategory2 = getRandomSubCategory(randomMainCategory.getId());

      // SubCategory - MainCategory
      randomSubCategory1.setMainCategory(randomMainCategory);
      randomSubCategory2.setMainCategory(randomMainCategory);

      subCategoryRepository.save(randomSubCategory1);
      subCategoryRepository.save(randomSubCategory2);

      // Space
      LocalTime openingTime = LocalTime.of(random.nextInt(9) + 1, 0);
      LocalTime closingTime = LocalTime.of(random.nextInt(5) + 18, 0);
      int hourlyRate = (random.nextInt(5) + 1) * 10_000;
      int spaceSize = random.nextInt(50) + 10;
      int maxCapacity = random.nextInt(20) + 1;

      System.out.println("randomSubCategory1 = " + randomSubCategory1);
      System.out.println("randomSubCategory2 = " + randomSubCategory2);
      System.out.println("hashtagMap.get(randomSubCategory1) = " + hashtagMap.get(randomSubCategory1.getName()));
      System.out.println("hashtagMap.get(randomSubCategory2) = " + hashtagMap.get(randomSubCategory2.getName()));

      Space spaceA = new Space(selectedDistrict + " " + selectedDong + " " + randomSubCategory1.getName() + " " + hashtagMap.get(randomSubCategory1.getName())[random.nextInt(10)] + " A" + i,
        spaceDescriptions.get(randomSubCategory1.getName()), reservationNote, openingTime,
        closingTime,
        hourlyRate, spaceSize,
        maxCapacity);

      Space spaceB = new Space(selectedDistrict + " " + selectedDong + " " + randomSubCategory2.getName() + " " + hashtagMap.get(randomSubCategory2.getName())[random.nextInt(10)] + " B" + i,
        spaceDescriptions.get(randomSubCategory2.getName()), reservationNote, openingTime,
        closingTime,
        hourlyRate, spaceSize,
        maxCapacity);

      // Space - RealEstate
      spaceA.setRealEstate(realEstate);
      spaceB.setRealEstate(realEstate);

      // Space - Subcategory
      spaceA.addSubCategory(randomSubCategory1);
      spaceA.addSubCategory(randomSubCategory2);
      spaceB.addSubCategory(randomSubCategory1);
      spaceB.addSubCategory(randomSubCategory2);

      // Space - Hashtag
      Set<Integer> nums = new HashSet<>();
      for (int idx = 0; idx < 10; ++idx) {
        nums.add(random.nextInt(10));
      }
      for (var e: nums) {
        String[] hashtags = hashtagMap.get(randomSubCategory1.getName());
        Hashtag hashtag = hashtagRepository.findByName(hashtags[e]).orElseGet(
          () -> {
            Hashtag h = new Hashtag(hashtags[e]);
            return hashtagRepository.save(h);
          });
        spaceA.addHashtag(hashtag);
        spaceB.addHashtag(hashtag);
      }

      spaceRepository.save(spaceA);
      spaceRepository.save(spaceB);

      // Space - Option
      Set<Option> randomOption = getRandomOption();
      for (var option : randomOption) {
        optionRepository.save(option);
        spaceA.addOption(option);
        spaceB.addOption(option);
      }

      User user = new User(name, name + i + "us" + "@" + emails[random.nextInt(emails.length)], passwordEncoder.encode("1234"),
        "172.21.0.3");
      user.getIpAddresses().add("220.93.17.226");
      user.getIpAddresses().add("127.0.0.1");
      user.payFeeForHost(-100_000);
      userRepository.save(user);
      LocalTime startTime = LocalTime.of(random.nextInt(3) + 9, 0);
      LocalTime endTime = LocalTime.of(random.nextInt(2) + 12, 0);
      LocalDate reservationDate = LocalDate.of(2024, 7, 31);

      long usageFeeA = Duration.between(startTime, endTime).toHours() * spaceA.getHourlyRate();
      Reservation reservationA = new Reservation(reservationDate, startTime, endTime, usageFeeA);
      reservationA.setSpace(spaceA);
      reservationA.setUser(user);

      long usageFeeB = Duration.between(startTime, endTime).toHours() * spaceB.getHourlyRate();
      Reservation reservationB = new Reservation(reservationDate, startTime, endTime, usageFeeB);
      reservationB.setSpace(spaceB);
      reservationB.setUser(user);

      reservationRepository.save(reservationA);
      reservationRepository.save(reservationB);
    }
  }

  private Set<Option> getRandomOption() {
    Random random = new Random();
    List<Option> options = optionRepository.findAll();
    Set<Option> selected = new HashSet<>();
    for (int i = 0; i < random.nextInt(10) + 3; ++i) {
      Option option = options.get(random.nextInt(options.size()));
      selected.add(option);
    }
    return selected;
  }

//  @Test
  void setOptions() {

    List<Option> options = Arrays.asList(
      new Option("TV/프로젝터"),
      new Option("인터넷/WIFI"),
      new Option("복사/인쇄기"),
      new Option("화이트보드"),
      new Option("음향/마이크"),
      new Option("취사시설"),
      new Option("음식물반입가능"),
      new Option("주류반입가능"),
      new Option("샤워시설"),
      new Option("주차"),
      new Option("금연"),
      new Option("반려동물 동반가능"),
      new Option("PC/노트북"),
      new Option("의자/테이블"),
      new Option("콘센트"),
      new Option("24시 운영"),
      new Option("연중무휴"),
      new Option("카페/레스토랑"),
      new Option("간단한 다과/음료"),
      new Option("개인락커"),
      new Option("메일 서비스"),
      new Option("공용 주방"),
      new Option("정수기"),
      new Option("케이터링"),
      new Option("난방기"),
      new Option("에어컨"),
      new Option("팩스"),
      new Option("창고서비스"),
      new Option("택배발송서비스"),
      new Option("내부화장실"),
      new Option("탈의실"),
      new Option("테라스/루프탑"),
      new Option("라운지/대기실"),
      new Option("전신거울"),
      new Option("바베큐시설"),
      new Option("도어락"),
      new Option("전기"),
      new Option("장비대여"),
      new Option("장작판매"),
      new Option("온수"),
      new Option("마트/편의점"),
      new Option("놀이터"),
      new Option("산책로"),
      new Option("구급약품"),
      new Option("남/여 화장실 구분"),
      new Option("급수시설")
    );

    for (var e : options) {
      optionRepository.save(e);
    }
  }

//  @Test
  void setUpCategories() {

    MainCategory together = new MainCategory("모임");
    mainCategoryRepository.save(together);

    SubCategory subCategory1 = new SubCategory("파티룸");
    subCategory1.setMainCategory(together);
    subCategoryRepository.save(subCategory1);

    SubCategory subCategory2 = new SubCategory("스터디룸");
    subCategory2.setMainCategory(together);
    subCategoryRepository.save(subCategory2);

    SubCategory subCategory3 = new SubCategory("공유주방");
    subCategory3.setMainCategory(together);
    subCategoryRepository.save(subCategory3);

    SubCategory subCategory4 = new SubCategory("회의실");
    subCategory4.setMainCategory(together);
    subCategoryRepository.save(subCategory4);

    SubCategory subCategory5 = new SubCategory("카페");
    subCategory5.setMainCategory(together);
    subCategoryRepository.save(subCategory5);

    SubCategory subCategory6 = new SubCategory("세미나실");
    subCategory6.setMainCategory(together);
    subCategoryRepository.save(subCategory6);

    SubCategory subCategory7 = new SubCategory("강의실");
    subCategory7.setMainCategory(together);
    subCategoryRepository.save(subCategory7);

    MainCategory practice = new MainCategory("연습");
    mainCategoryRepository.save(practice);

    SubCategory subCategory8 = new SubCategory("연습실");
    subCategory8.setMainCategory(practice);
    subCategoryRepository.save(subCategory8);

    SubCategory subCategory9 = new SubCategory("댄스연습실");
    subCategory9.setMainCategory(practice);
    subCategoryRepository.save(subCategory9);

    SubCategory subCategory10 = new SubCategory("보컬연습실");
    subCategory10.setMainCategory(practice);
    subCategoryRepository.save(subCategory10);

    SubCategory subCategory11 = new SubCategory("악기연습실");
    subCategory11.setMainCategory(practice);
    subCategoryRepository.save(subCategory11);

    SubCategory subCategory12 = new SubCategory("운동시설");
    subCategory12.setMainCategory(practice);
    subCategoryRepository.save(subCategory12);

    SubCategory subCategory13 = new SubCategory("녹음실");
    subCategory13.setMainCategory(practice);
    subCategoryRepository.save(subCategory13);

    MainCategory picture = new MainCategory("촬영");
    mainCategoryRepository.save(picture);


    SubCategory subCategory14 = new SubCategory("촬영스튜디오");
    subCategory14.setMainCategory(picture);
    subCategoryRepository.save(subCategory14);

    SubCategory subCategory15 = new SubCategory("렌탈스튜디오");
    subCategory15.setMainCategory(picture);
    subCategoryRepository.save(subCategory15);

    SubCategory subCategory16 = new SubCategory("라이브방송");
    subCategory16.setMainCategory(picture);
    subCategoryRepository.save(subCategory16);

    SubCategory subCategory17 = new SubCategory("호리존");
    subCategory17.setMainCategory(picture);
    subCategoryRepository.save(subCategory17);

    SubCategory subCategory18 = new SubCategory("실외촬영");
    subCategory18.setMainCategory(picture);
    subCategoryRepository.save(subCategory18);

    SubCategory subCategory19 = new SubCategory("가정집");
    subCategory19.setMainCategory(picture);
    subCategoryRepository.save(subCategory19);

    MainCategory event = new MainCategory("행사");
    mainCategoryRepository.save(event);

    SubCategory subCategory20 = new SubCategory("공연장");
    subCategory20.setMainCategory(event);
    subCategoryRepository.save(subCategory20);

    SubCategory subCategory21 = new SubCategory("컨퍼런스");
    subCategory21.setMainCategory(event);
    subCategoryRepository.save(subCategory21);

    SubCategory subCategory22 = new SubCategory("스몰웨딩");
    subCategory22.setMainCategory(event);
    subCategoryRepository.save(subCategory22);

    SubCategory subCategory23 = new SubCategory("갤러리");
    subCategory23.setMainCategory(event);
    subCategoryRepository.save(subCategory23);

    MainCategory office = new MainCategory("오피스");
    mainCategoryRepository.save(office);

    SubCategory subCategory24 = new SubCategory("독립오피스");
    subCategory24.setMainCategory(office);
    subCategoryRepository.save(subCategory24);

    SubCategory subCategory25 = new SubCategory("코워킹오피스");
    subCategory25.setMainCategory(office);
    subCategoryRepository.save(subCategory25);
  }
}

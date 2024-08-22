package com.juny.spacestory.space.domain;

import com.juny.spacestory.audit.SoftDeleteBaseEntity;
import com.juny.spacestory.detailed_space.DetailedSpace;
import com.juny.spacestory.qna.domain.Question;
import com.juny.spacestory.category.SubCategory;
import com.juny.spacestory.space.domain.dayoff.DesignatedDayOff;
import com.juny.spacestory.space.domain.dayoff.RegularDayOff;
import com.juny.spacestory.space.domain.hashtag.Hashtag;
import com.juny.spacestory.space.domain.images.SpaceImagePath;
import com.juny.spacestory.space.domain.option.Option;
import com.juny.spacestory.space.domain.space_option.SpaceOption;
import com.juny.spacestory.space.dto.ReqSpace;
import com.juny.spacestory.user.domain.Host;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalTime;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "spaces")
public class Space extends SoftDeleteBaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String description;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String reservationNotes;

  @Column(nullable = false)
  private LocalTime openingTime;

  @Column(nullable = false)
  private LocalTime closingTime;

  @Column
  private Address address;

  @Column(nullable = false)
  private Integer floor;

  @Column(nullable = false)
  private Boolean hasParking;

  @Column(nullable = false)
  private Boolean hasElevator;

  @Column(nullable = false)
  private Integer likeCount;

  @Column(nullable = false)
  private Long viewCount;

  @Column(nullable = false)
  private Integer reviewCount;

  @Column
  private String representImage;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "host_id")
  private Host host;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  private List<SpaceImagePath> spaceImagePaths = new ArrayList<>();

  @OneToMany(mappedBy = "space", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<SpaceOption> spaceOptions = new ArrayList<>();

  @OneToMany(mappedBy = "space", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Question> questions = new ArrayList<>();

  @OneToMany(mappedBy = "space", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<DetailedSpace> detailedSpaces = new ArrayList<>();

  @OneToMany(mappedBy = "space", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<RegularDayOff> regularDayOffs = new ArrayList<>();

  @OneToMany(mappedBy = "space", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<DesignatedDayOff> designatedDayOffs = new ArrayList<>();

  @ManyToMany
  @JoinTable(
    name = "space_sub_categories",
    joinColumns = @JoinColumn(name = "space_id"),
    inverseJoinColumns = @JoinColumn(name = "sub_category_id")
  )
  private List<SubCategory> subCategories = new ArrayList<>();

  @ManyToMany
  @JoinTable(
    name = "space_hashtags",
    joinColumns = @JoinColumn(name = "space_id"),
    inverseJoinColumns = @JoinColumn(name = "hashtag_id")
  )
  private List<Hashtag> hashtags = new ArrayList<>();

  public Space(String name, String description, String reservationNotes, LocalTime openingTime, LocalTime closingTime) {
    this.name = name;
    this.description = description;
    this.reservationNotes = reservationNotes;
    this.openingTime = openingTime;
    this.closingTime = closingTime;
    this.likeCount = 0;
    this.viewCount = 0L;
    this.reviewCount = 0;
  }

  // ManyToOne 연관관계 편의 메서드, 공간 - 호스트 [양방향]
  public void setHost(Host host) {
    if (this.host != null) {
      this.host.getSpaces().remove(this);
    }
    this.host = host;
    if (host != null && !host.getSpaces().contains(this)) {
      this.host.getSpaces().add(this);
    }
  }

  // OneToMany 연관관계 편의 메서드, 공간 - 이미지 [단방향]
  public void addSpaceImagePath(SpaceImagePath spaceImagePath) {
    spaceImagePaths.add(spaceImagePath);
  }

  // OneToMany 연관관계 편의 메서드, 공간 - 이미지 [단방향]
  public void removeSpaceImagePath(SpaceImagePath spaceImagePath) {
    spaceImagePaths.remove(spaceImagePath);
  }

  // OneToMany 연관관계 편의 메서드, 공간 - 정기휴무일 [단방향]
  public void addRegularDayOff(RegularDayOff regularDayOff) {
    regularDayOffs.add(regularDayOff);
  }

  // OneToMany 연관관계 편의 메서드, 공간 - 정기휴무일 [단방향]
  public void removeRegularDayOff(RegularDayOff regularDayOff) {
    regularDayOffs.remove(regularDayOff);
  }

  // OneToMany 연관관계 편의 메서드, 공간 - 지정휴무일 [단방향]
  public void addDesignatedDayOff(DesignatedDayOff designatedDayOff) {
    designatedDayOffs.add(designatedDayOff);
  }

  // OneToMany 연관관계 편의 메서드, 공간 - 지정휴무일 [단방향]
  public void removeDesignatedDayOff(DesignatedDayOff designatedDayOff) {
    designatedDayOffs.remove(designatedDayOff);
  }

  // OneToMany 연관관계 편의 메서드, 공간 - 질문 [양방향]
  public void addQuestion(Question question) {
    this.questions.add(question);
    if (question.getSpace() != this) {
      question.setSpace(this);
    }
  }

  // OneToMany 연관관계 편의 메서드, 공간 - 질문 [양방향]
  public void removeQuestion(Question question) {
    this.questions.remove(question);
    if (question.getSpace() == this) {
      question.setSpace(null);
    }
  }

  // OneToMany 연관관계 편의 메서드, 공간 - 상세공간 [양방향]
  public void addDetailedSpace(DetailedSpace detailedSpace) {
    this.detailedSpaces.add(detailedSpace);
    if (detailedSpace.getSpace() != this) {
      detailedSpace.setSpace(this);
    }
  }

  // OneToMany 연관관계 편의 메서드, 공간 - 상세공간 [양방향]
  public void removeDetailedSpace(DetailedSpace detailedSpace) {
    this.detailedSpaces.remove(detailedSpace);
    if (detailedSpace.getSpace() == this) {
      detailedSpace.setSpace(null);
    }
  }

  // ManyToMany[중간 테이블 X] 연관관계 편의 메서드, 공간 - 서브카테고리 [양방향]
  public void addSubCategory(SubCategory subCategory) {
    if (!this.subCategories.contains(subCategory)) {
      this.subCategories.add(subCategory);
      subCategory.addSpace(this);
    }
  }

  // ManyToMany[중간 테이블 X] 연관관계 편의 메서드, 공간 - 서브카테고리 [양방향]
  public void removeSubCategory(SubCategory subCategory) {
    if (this.subCategories.remove(subCategory)) {
      subCategory.removeSpace(this);
    }
  }

  // ManyToMany[중간 테이블 O] 연관관계 편의 메서드, 공간 - 옵션 [양방향]
  public void addOption(Option option) {
    SpaceOption spaceOption = new SpaceOption(this, option);
    spaceOptions.add(spaceOption);
    option.getSpaceOptions().add(spaceOption);
  }

  // ManyToMany[중간 테이블 O] 연관관계 편의 메서드, 공간 - 옵션 [양방향]
  public void removeOption(Option option) {
    SpaceOption spaceOption = new SpaceOption(this, option);

    if (spaceOptions.remove(spaceOption)) {
      option.getSpaceOptions().remove(spaceOption);
      spaceOption.setSpace(null);
      spaceOption.setOption(null);
    }
  }

  // ManyToMany[중간 테이블 X] 연관관계 편의 메서드, 공간 - 해시태그 [양방향]
  public void addHashtag(Hashtag hashtag) {
    if (!this.hashtags.contains(hashtag)) {
      this.hashtags.add(hashtag);
      hashtag.addSpace(this);
    }
  }

  // ManyToMany[중간 테이블 X] 연관관계 편의 메서드, 공간 - 해시태그 [양방향]
  public void removeHashtag(Hashtag hashtag) {
    if (this.hashtags.remove(hashtag)) {
      hashtag.removeSpace(this);
    }
  }

  public void updateSpace(ReqSpace req) {
    this.name = req.name();
    this.openingTime = req.openingTime();
    this.closingTime = req.closingTime();
  }

  public void increaseViewCount() {
    this.viewCount++;
  }

  public void setRepresentImage(String representImage) {
    this.representImage = representImage;
  }
}

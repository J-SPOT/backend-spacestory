package com.juny.spacestory.space.domain;

import com.juny.spacestory.space.domain.category.SubCategory;
import com.juny.spacestory.space.domain.hashtag.Hashtag;
import com.juny.spacestory.space.domain.option.Option;
import com.juny.spacestory.space.domain.realestate.RealEstate;
import com.juny.spacestory.space.domain.space_option.SpaceOption;
import com.juny.spacestory.space.dto.ReqSpace;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Table(name = "spaces")
public class Space {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String description;

  @Column(nullable = false)
  private String reservationNotes;

  @Column(nullable = false)
  private LocalTime openingTime;

  @Column(nullable = false)
  private LocalTime closingTime;

  @Column(nullable = false)
  private Integer hourlyRate;

  @Column(nullable = false)
  private Integer spaceSize;

  @Column(nullable = false)
  private Integer maxCapacity;

  @Column(nullable = false)
  private LocalDateTime createdAt;

  @Column
  private LocalDateTime deletedAt;

  @Column(nullable = false)
  private Integer likeCount;

  @Column(nullable = false)
  private Integer viewCount;

  @Column(nullable = false)
  private Integer reviewCount;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "realEstate_id")
  private RealEstate realEstate;

  @ManyToMany
  @JoinTable(
    name = "space_sub_categories",
    joinColumns = @JoinColumn(name = "space_id"),
    inverseJoinColumns = @JoinColumn(name = "sub_category_id")
  )
  private List<SubCategory> subCategories = new ArrayList<>();

  @OneToMany(mappedBy = "space", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<SpaceOption> spaceOptions = new ArrayList<>();

  @ManyToMany
  @JoinTable(
    name = "space_hashtags",
    joinColumns = @JoinColumn(name = "space_id"),
    inverseJoinColumns = @JoinColumn(name = "hashtag_id")
  )
  private List<Hashtag> hashtags = new ArrayList<>();

  public Space(String name, String description, String reservationNotes, LocalTime openingTime, LocalTime closingTime, Integer hourlyRate,
    Integer spaceSize, Integer maxCapacity) {
    this.name = name;
    this.description = description;
    this.reservationNotes = reservationNotes;
    this.openingTime = openingTime;
    this.closingTime = closingTime;
    this.hourlyRate = hourlyRate;
    this.spaceSize = spaceSize;
    this.maxCapacity = maxCapacity;
    this.likeCount = 0;
    this.viewCount = 0;
    this.reviewCount = 0;
    this.deletedAt = null;
    this.createdAt = LocalDateTime.now();
  }

  // 연관관계 편의 메서드
  public void setRealEstate(RealEstate realEstate) {
    this.realEstate = realEstate;
  }

  // 연관관계 편의 메서드
  public void addSubCategory(SubCategory subCategory) {
    if (!this.subCategories.contains(subCategory)) {
      this.subCategories.add(subCategory);
      subCategory.addSpace(this);
    }
  }

  // 연관관계 편의 메서드
  public void removeSubCategory(SubCategory subCategory) {
    if (this.subCategories.contains(subCategory)) {
      this.subCategories.remove(subCategory);
      subCategory.removeSpace(this);
    }
  }

  // 연관관계 편의 메서드
  public void addOption(Option option) {
    SpaceOption spaceOption = new SpaceOption(this, option);
    spaceOptions.add(spaceOption);
    option.getSpaceOptions().add(spaceOption);
  }

  // 연관관계 편의 메서드
  public void removeOption(Option option) {
    for (Iterator<SpaceOption> iterator = spaceOptions.iterator(); iterator.hasNext(); ) {
      SpaceOption spaceOption = iterator.next();
      if (spaceOption.getOption().equals(option)) {
        iterator.remove();
        option.getSpaceOptions().remove(spaceOption);
        spaceOption.setSpace(null);
        spaceOption.setOption(null);
      }
    }
  }

  // 연관관계 편의 메서드
  public void addHashtag(Hashtag hashtag) {
    if (!this.hashtags.contains(hashtag)) {
      this.hashtags.add(hashtag);
      hashtag.addSpace(this);
    }
  }

  // 연관관계 편의 메서드
  public void removeHashtag(Hashtag hashtag) {
    if (this.hashtags.contains(hashtag)) {
      this.hashtags.remove(hashtag);
      hashtag.removeSpace(this);
    }
  }

  public void updateSpace(ReqSpace req) {
    this.name = req.name();
    this.description = req.description();
    this.reservationNotes = req.reservationNotes();
    this.openingTime = req.openingTime();
    this.closingTime = req.closingTime();
    this.hourlyRate = req.hourlyRate();
    this.spaceSize = req.spaceSize();
    this.maxCapacity = req.maxCapacity();
  }

  public void increaseViewCount() {
    this.viewCount++;
  }
}

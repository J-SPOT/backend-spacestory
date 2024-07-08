package com.juny.spacestory.space.domain;

import com.juny.spacestory.space.domain.category.SubCategory;
import com.juny.spacestory.space.domain.hashtag.Hashtag;
import com.juny.spacestory.space.domain.option.Option;
import com.juny.spacestory.space.domain.realestate.RealEstate;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Space {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String spaceName;

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
  private String spaceDescription;

  @Column private Integer reviewCount;

  @Column(nullable = false)
  private LocalDateTime deletedAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "realEstate_id")
  private RealEstate realEstate;

  @ManyToMany
  @JoinTable(
    name = "space_sub_categories",
    joinColumns = @JoinColumn(name = "space_id"),
    inverseJoinColumns = @JoinColumn(name = "sub_category_id")
  )
  private List<SubCategory> subCategories;

  @ManyToMany
  @JoinTable(
    name = "space_options",
    joinColumns = @JoinColumn(name = "space_id"),
    inverseJoinColumns = @JoinColumn(name = "option_id")
  )
  private List<Option> options;

  @ManyToMany
  @JoinTable(
    name = "space_hashtags",
    joinColumns = @JoinColumn(name = "space_id"),
    inverseJoinColumns = @JoinColumn(name = "hashtag_id")
  )
  private List<Hashtag> hashtags;

  public Space(String spaceName, LocalTime openingTime, LocalTime closingTime, Integer hourlyRate,
    Integer spaceSize, Integer maxCapacity, String spaceDescription) {
    this.spaceName = spaceName;
    this.openingTime = openingTime;
    this.closingTime = closingTime;
    this.hourlyRate = hourlyRate;
    this.spaceSize = spaceSize;
    this.maxCapacity = maxCapacity;
    this.spaceDescription = spaceDescription;
    this.reviewCount = 0;
    this.deletedAt = null;
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
    if (!this.options.contains(option)) {
      this.options.add(option);
      option.addSpace(this);
    }
  }

  // 연관관계 편의 메서드
  public void removeOption(Option option) {
    if (this.options.contains(option)) {
      this.options.remove(option);
      option.removeSpace(this);
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

//  public void softDelete(Space space) {
//    this.deletedAt = LocalDateTime.now();
//  }
}

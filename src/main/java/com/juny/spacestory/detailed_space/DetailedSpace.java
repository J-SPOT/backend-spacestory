package com.juny.spacestory.detailed_space;

import com.juny.spacestory.category.SubCategory;
import com.juny.spacestory.reservation.entity.prices.ReservationInfo;
import com.juny.spacestory.space.domain.Space;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "detailed_spaces")
public class DetailedSpace {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column
  private String name;

  @Column
  @Enumerated(EnumType.STRING)
  private ReservationType represestReservationType;

  @Column
  private Integer representRate;

  @Column
  private String representImage;

  @Column
  private String description;

  @Column(nullable = false)
  private Integer size;

  @Column
  private Integer minimumReservationTime;

  @Column
  private Integer minimumCapacity;

  @Column
  private Integer maximumCapacity;

  @Column
  private LocalDateTime createdAt;

  @Column
  private LocalDateTime deletedAt;

  @ManyToOne
  @JoinColumn(name = "space_id")
  private Space space;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  private List<DetailedSpaceImagePath> detailedSpaceImagePaths = new ArrayList<>();

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ReservationInfo> reservationInfos = new ArrayList<>();

  @ManyToMany
  @JoinTable(
    name = "detailed_space_sub_categories",
    joinColumns = @JoinColumn(name = "detailed_space_id"),
    inverseJoinColumns = @JoinColumn(name = "sub_category_id")
  )
  private List<SubCategory> subCategories = new ArrayList<>();

  // ManyToOne 연관관계 편의 메서드, 상세공간 - 공간 [양방향]
  public void setSpace(Space space) {
    if (this.space != null) {
      this.space.getDetailedSpaces().remove(this);
    }
    this.space = space;
    if (space != null && !space.getDetailedSpaces().contains(this)) {
      space.getDetailedSpaces().add(this);
    }
  }

  // OneToMany 연관관계 편의 메서드, 상세공간 - 이미지 [단방향]
  public void addSpaceImagePath(DetailedSpaceImagePath imagePath) {
    detailedSpaceImagePaths.add(imagePath);
  }

  // OneToMany 연관관계 편의 메서드, 상세공간 - 이미지 [단방향]
  public void removeSpaceImagePath(DetailedSpaceImagePath imagePath) {
    detailedSpaceImagePaths.remove(imagePath);
  }

  // OneToMany 연관관계 편의 메서드, 상세공간 - 예약정보 [양방향]
  public void addReservationInfo(ReservationInfo reservationInfo) {
    this.reservationInfos.add(reservationInfo);
    if (reservationInfo.getDetailedSpace() != this) {
      reservationInfo.setDetailedSpace(this);
    }
  }

  // OneToMany 연관관계 편의 메서드, 상세공간 - 예약정보 [양방향]
  public void removeReservationInfo(ReservationInfo reservationInfo) {
    this.reservationInfos.remove(reservationInfo);
    if (reservationInfo.getDetailedSpace() == this) {
      reservationInfo.setDetailedSpace(null);
    }
  }

  // ManyToMany[중간 테이블 X] 연관관계 편의 메서드, 상세공간 - 서브카테고리 [양방향]
  public void addSubCategory(SubCategory subCategory) {
    if (!this.subCategories.contains(subCategory)) {
      this.subCategories.add(subCategory);
      subCategory.addDetailedSpace(this);
    }
  }

  // ManyToMany[중간 테이블 X] 연관관계 편의 메서드, 상세공간 - 서브카테고리 [양방향]
  public void removeSubCategory(SubCategory subCategory) {
    if (this.subCategories.remove(subCategory)) {
      subCategory.removeDetailedSpace(this);
    }
  }

  public DetailedSpace(String name, ReservationType reservationType, Integer rate, String representImage,
    String description, Integer size, Integer minimumCapacity, Integer maximumCapacity) {

    this.name = name;
    this.represestReservationType = reservationType;
    this.representRate = rate;
    this.representImage = representImage;
    this.description = description;
    this.size = size;
    this.minimumCapacity = minimumCapacity;
    this.maximumCapacity = maximumCapacity;
    this.createdAt = LocalDateTime.now();
  }
}

package com.juny.spacestory.detailed_space;

import com.juny.spacestory.space.domain.Space;
import com.juny.spacestory.space.domain.images.SpaceImagePath;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
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
  private RateType represestRateType;

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

  // 연관관계 편의 메서드
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

  public DetailedSpace(String name, RateType rateType, Integer rate, String representImage,
    String description, Integer size, Integer minimumCapacity, Integer maximumCapacity) {

    this.name = name;
    this.represestRateType = rateType;
    this.representRate = rate;
    this.representImage = representImage;
    this.description = description;
    this.size = size;
    this.minimumCapacity = minimumCapacity;
    this.maximumCapacity = maximumCapacity;
    this.createdAt = LocalDateTime.now();
  }
}

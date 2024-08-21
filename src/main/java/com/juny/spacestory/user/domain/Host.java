package com.juny.spacestory.user.domain;

import com.juny.spacestory.space.domain.Space;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "hosts")
public class Host {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column
  private String businessName;

  @Column
  private String representativeName;

  @Column
  private String officeStreetAddress;

  @Column
  private String officeDetailAddress;

  @Column
  private String businessEmail;

  @Column
  private String businessPhoneNumber;

  @OneToOne(mappedBy = "host")
  private User user;

  @OneToMany(mappedBy = "host", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Space> spaces = new ArrayList<>();

  // OneToOne 연관관계 편의 메서드, 호스트 - 유저 [양방향]
  public void setUser(User user) {
    this.user = user;
    user.setHost(this);
  }

  // OneToMany 연관관계 편의 메서드, 호스트 - 공간 [양방향]
  public void addSpace(Space space) {
    this.spaces.add(space);
    if (space.getHost() != this) {
      space.setHost(this);
    }
  }

  // OneToMany 연관관계 편의 메서드, 호스트 - 공간 [양방향]
  public void removeSpace(Space space) {
    this.spaces.remove(space);
    if (space.getHost() == this) {
      space.setHost(null);
    }
  }
}

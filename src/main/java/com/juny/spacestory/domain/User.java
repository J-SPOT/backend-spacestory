package com.juny.spacestory.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString(exclude = "spaceReservations")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userName;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false, unique = true)
    private String nickName;

    @Column(nullable = false)
    private Integer point;

    @OneToMany(mappedBy = "user", orphanRemoval = true)
    private Set<SpaceReservation> spaceReservations = new HashSet<>();

    public User(String userName, String email, String nickName, Integer point, Set<SpaceReservation> spaceReservations) {
        this.userName = userName;
        this.email = email;
        this.nickName = nickName;
        this.point = point;
        this.spaceReservations = spaceReservations;
    }
}

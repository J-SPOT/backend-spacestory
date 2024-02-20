package com.juny.spacestory.domain;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
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

    @OneToMany(mappedBy = "user", orphanRemoval = true)
    private Set<SpaceReservation> reservations = new HashSet<>();
}

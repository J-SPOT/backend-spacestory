package com.juny.spacestory.domain;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
public class Space {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private SpaceType spaceType;

    private Integer spaceSize;

    @Column(nullable = false)
    private Integer maxCapacity;

    @ManyToOne
    @JoinColumn(name = "realEstate_id")
    private RealEstate realEstate;

    @OneToMany(mappedBy = "space")
    private Set<SpaceTypeDetail> spaceTypeDetails = new HashSet<>();

    @OneToMany(mappedBy = "space", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SpaceReservation> reservations = new HashSet<>();
}

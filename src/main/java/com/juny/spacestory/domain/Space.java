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
@ToString(exclude = {"spaceTypeDetails", "spaceReservations"})
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
    private Set<SpaceReservation> spaceReservations = new HashSet<>();

    public Space(SpaceType spaceType, Integer spaceSize, Integer maxCapacity, RealEstate realEstate, Set<SpaceTypeDetail> spaceTypeDetails, Set<SpaceReservation> spaceReservations) {
        this.spaceType = spaceType;
        this.spaceSize = spaceSize;
        this.maxCapacity = maxCapacity;
        this.realEstate = realEstate;
        this.spaceTypeDetails = spaceTypeDetails;
        this.spaceReservations = spaceReservations;
    }
}

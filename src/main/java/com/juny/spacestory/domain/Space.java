package com.juny.spacestory.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Space {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private SpaceType spaceType;

    @Column(nullable = false)
    private String spaceName;

    @Column(nullable = false)
    private LocalTime openingTime;

    @Column(nullable = false)
    private LocalTime closingTime;

    @Column(nullable = false)
    private Integer hourlyRate;

    private Integer spaceSize;

    @Column(nullable = false)
    private Integer maxCapacity;

    @ManyToOne
    @JoinColumn(name = "realEstate_id")
    private RealEstate realEstate;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "space_id")
    private Set<DetailedType> detailedTypes = new HashSet<>();

    public Space(SpaceType spaceType, String spaceName, LocalTime openingTime, LocalTime closingTime, Integer hourlyRate, Integer spaceSize, Integer maxCapacity, RealEstate realEstate, Set<DetailedType> detailedTypes) {
        this.spaceType = spaceType;
        this.spaceName = spaceName;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.hourlyRate = hourlyRate;
        this.spaceSize = spaceSize;
        this.maxCapacity = maxCapacity;
        this.realEstate = realEstate;
        this.detailedTypes = detailedTypes;
    }
}

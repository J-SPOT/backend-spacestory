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

    @Column(nullable = false)
    private Integer spaceSize;

    @Column(nullable = false)
    private Integer maxCapacity;

    @ElementCollection(fetch = FetchType.LAZY, targetClass = DetailedType.class)
    @CollectionTable(name = "space_detailed_type", joinColumns = @JoinColumn(name = "space_id"))
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Set<DetailedType> detailedTypes = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "realEstate_id")
    private RealEstate realEstate;

    public Space(SpaceType spaceType, String spaceName, LocalTime openingTime, LocalTime closingTime, Integer hourlyRate, Integer spaceSize, Integer maxCapacity, Set<DetailedType> detailedTypes, RealEstate realEstate) {
        this.spaceType = spaceType;
        this.spaceName = spaceName;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.hourlyRate = hourlyRate;
        this.spaceSize = spaceSize;
        this.maxCapacity = maxCapacity;
        this.detailedTypes = detailedTypes;
        this.realEstate = realEstate;
    }
}

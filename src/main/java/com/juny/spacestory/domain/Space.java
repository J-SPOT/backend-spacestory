package com.juny.spacestory.domain;

import com.juny.spacestory.dto.RequestUpdateSpace;
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

    @Column(nullable = false)
    private String spaceDescription;

    @Column(nullable = false)
    private Boolean isDeleted;

    @ElementCollection(fetch = FetchType.LAZY, targetClass = DetailedType.class)
    @CollectionTable(name = "space_detailed_type", joinColumns = @JoinColumn(name = "space_id"))
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Set<DetailedType> detailedTypes = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "realEstate_id")
    private RealEstate realEstate;

    public Space(SpaceType spaceType, String spaceName, LocalTime openingTime, LocalTime closingTime, Integer hourlyRate, Integer spaceSize, Integer maxCapacity, String spaceDescription, Boolean isDeleted, Set<DetailedType> detailedTypes, RealEstate realEstate) {
        this.spaceType = spaceType;
        this.spaceName = spaceName;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.hourlyRate = hourlyRate;
        this.spaceSize = spaceSize;
        this.maxCapacity = maxCapacity;
        this.spaceDescription = spaceDescription;
        this.isDeleted = isDeleted;
        this.detailedTypes = detailedTypes;
        this.realEstate = realEstate;
    }

    public void updateSpace(RequestUpdateSpace req) {
        this.spaceType = req.spaceType();
        this.spaceName = req.spaceName();
        this.openingTime = req.openingTime();
        this.closingTime = req.closingTime();
        this.hourlyRate = req.hourlyRate();
        this.spaceSize = req.spaceSize();
        this.maxCapacity = req.maxCapacity();
        this.spaceDescription = req.spaceDescription();
        this.isDeleted = req.isDeleted();
        this.detailedTypes = req.detailedTypes();
    }

    public void softDelete(Space space) {
        space.isDeleted = true;
    }
}
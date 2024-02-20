package com.juny.spacestory.domain;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class RealEstate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Addresss address;

    @Column(nullable = false)
    private Integer floor;

    @Column(nullable = false)
    private Boolean hasParking;

    @Column(nullable = false)
    private Boolean hasElevator;

    @ManyToOne
    @JoinColumn(name = "host_id")
    private Host host;

    @OneToMany(mappedBy = "realEstate", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Space> spaces = new HashSet<>();
}

package com.juny.spacestory.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString(exclude = "spaces")
public class RealEstate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Address address;

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

    public RealEstate(Address address, Integer floor, Boolean hasParking, Boolean hasElevator, Host host, Set<Space> spaces) {
        this.address = address;
        this.floor = floor;
        this.hasParking = hasParking;
        this.hasElevator = hasElevator;
        this.host = host;
        this.spaces = spaces;
    }
}

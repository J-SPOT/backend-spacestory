package com.juny.spacestory.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString(exclude = "realEstates")
public class Host {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userName;

    @Column(nullable = false)
    private Integer point;

    @OneToMany(mappedBy = "host", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RealEstate> realEstates = new HashSet<>();

    public Host(String userName, Integer point, Set<RealEstate> realEstates) {
        this.userName = userName;
        this.point = point;
        this.realEstates = realEstates;
    }
}

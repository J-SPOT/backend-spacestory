package com.juny.spacestory.domain;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
public class TypeDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String detailsName;

    @OneToMany(mappedBy = "typeDetail")
    private Set<SpaceTypeDetail> spaceTypeDetails = new HashSet<>();
}

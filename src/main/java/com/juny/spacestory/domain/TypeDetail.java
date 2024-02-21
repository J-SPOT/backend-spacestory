package com.juny.spacestory.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "spaceTypeDetails")
public class TypeDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String detailsName;

    @OneToMany(mappedBy = "typeDetail")
    private Set<SpaceTypeDetail> spaceTypeDetails = new HashSet<>();

    public TypeDetail(String detailsName, Set<SpaceTypeDetail> spaceTypeDetails) {
        this.detailsName = detailsName;
        this.spaceTypeDetails = spaceTypeDetails;
    }
}

package com.juny.spacestory.domain;

import jakarta.persistence.*;

@Entity
public class SpaceTypeDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "space_id")
    private Space space;

    @ManyToOne
    @JoinColumn(name = "typeDetail_id")
    private TypeDetail typeDetail;
}

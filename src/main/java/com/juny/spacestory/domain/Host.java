package com.juny.spacestory.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class Host {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userName;

    @Column(nullable = false)
    private Long point;

    @Column(nullable = false)
    private Boolean isDeleted;

    public Host(String userName, Long point, Boolean isDeleted) {
        this.userName = userName;
        this.point = point;
        this.isDeleted = isDeleted;
    }

    public void receivedFee(long usageFee) {
        point += usageFee;
    }
}

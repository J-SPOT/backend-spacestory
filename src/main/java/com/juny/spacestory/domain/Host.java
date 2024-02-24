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

    public Host(String userName, Long point) {
        this.userName = userName;
        this.point = point;
    }

    public void receivedFee(long usageFee) {
        point += usageFee;
    }
}

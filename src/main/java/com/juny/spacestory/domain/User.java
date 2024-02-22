package com.juny.spacestory.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userName;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false, unique = true)
    private String nickName;

    @Column(nullable = false)
    private Long point;

    public User(String userName, String email, String nickName, Long point) {
        this.userName = userName;
        this.email = email;
        this.nickName = nickName;
        this.point = point;
    }

    public void payFee(long usageFee, Host host) {
        if (this.point < usageFee) {
            throw new IllegalArgumentException("사용자의 포인트가 부족합니다.");
        }
        this.point -= usageFee;
        host.receivedFee(usageFee);
    }
}

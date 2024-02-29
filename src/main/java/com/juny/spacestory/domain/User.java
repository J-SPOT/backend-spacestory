package com.juny.spacestory.domain;

import com.juny.spacestory.exception.global.ErrorCode;
import com.juny.spacestory.exception.user.UserExceededPointException;
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

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String nickName;

    @Column(nullable = false)
    private Long point;

    @Column(nullable = false)
    private Boolean isDeleted;

    public User(String userName, String email, String nickName, Long point, Boolean isDeleted) {
        this.userName = userName;
        this.email = email;
        this.nickName = nickName;
        this.point = point;
        this.isDeleted = isDeleted;
    }

    public void payFee(long usageFee, Host host) {
        if (this.point < usageFee) {
            throw new UserExceededPointException(ErrorCode.USER_NOT_ENOUGH_POINT);
        }
        this.point -= usageFee;
        host.receivedFee(usageFee);
    }

    public void getRefund(long differenceAmount, Host host) {
        this.point += differenceAmount;
        host.receivedFee(-differenceAmount);
    }
}

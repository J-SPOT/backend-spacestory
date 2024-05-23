package com.juny.spacestory.domain;

import com.juny.spacestory.dto.RequestUpdateReview;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private Double rating;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Boolean isDeleted;

    public Review(String content, Double rating, Long userId, Boolean isDeleted) {
        this.content = content;
        this.rating = rating;
        this.userId = userId;
        this.isDeleted = isDeleted;
    }

    public void update(RequestUpdateReview req) {
        this.content = req.content();
        this.rating = req.rating();
    }

    public void softDelete(Review review) {
        this.isDeleted = true;
    }
}

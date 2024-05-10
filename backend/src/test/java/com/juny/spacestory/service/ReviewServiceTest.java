package com.juny.spacestory.service;

import com.juny.spacestory.domain.Review;
import com.juny.spacestory.domain.User;
import com.juny.spacestory.dto.RequestCreateReview;
import com.juny.spacestory.dto.RequestUpdateReview;
import com.juny.spacestory.dto.ResponseReview;
import com.juny.spacestory.mapper.ReviewMapper;
import com.juny.spacestory.repository.RealEstateRepository;
import com.juny.spacestory.repository.ReviewRepository;
import com.juny.spacestory.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceTest {

    @InjectMocks
    private ReviewService reviewService;

    @Mock
    ReviewRepository reviewRepository;

    @Mock
    UserRepository userRepository;

    private final ReviewMapper mapper = Mappers.getMapper(ReviewMapper.class);

    private User user;
    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(reviewService, "mapper", mapper);
        user = new User("user1", "user1@gmail.com", "nickname1", 100_000L, false);
    }
    @DisplayName("리뷰를 등록한다.")
    @Test
    void createReview() {
        //given
        RequestCreateReview req = new RequestCreateReview("comment1", 4.5, user.getId());
        Review expectedReview = new Review("comment1", 4.5, user.getId(), false);
        ResponseReview expected = mapper.ReviewToResponseReview(expectedReview);

        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(reviewRepository.save(any(Review.class))).thenReturn(expectedReview);

        //when
        ResponseReview review = reviewService.create(req);

        //then
        assertThat(review).isNotNull();
        assertThat(review).usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("리뷰를 수정한다.")
    @Test
    void updateReview() {
        //given
        RequestUpdateReview req = new RequestUpdateReview("comment1", 4.5);
        Review expectedReview = new Review("comment1", 4.5, user.getId(), false);
        ResponseReview expected = mapper.ReviewToResponseReview(expectedReview);

        Review before = new Review("origin", 4.5, user.getId(), false);

        when(reviewRepository.findById(any())).thenReturn(Optional.of(before));
        when(reviewRepository.save(before)).thenReturn(expectedReview);

        //when
        ResponseReview updatedReview = reviewService.update(any(), req);

        //then
        assertThat(updatedReview).isNotNull();
        assertThat(updatedReview).usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("리뷰를 삭제한다.")
    @Test
    void deleteReview() {
        //given
        Review expectedReview = new Review("comment1", 4.5, user.getId(), false);
        ResponseReview expected = mapper.ReviewToResponseReview(expectedReview);

        when(reviewRepository.findById(any())).thenReturn(Optional.of(expectedReview));

        //when
        reviewService.delete(any());

        //then
        verify(reviewRepository).findById(any());
        assertThat(expectedReview.getIsDeleted()).isEqualTo(true);
    }
}

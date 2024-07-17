package com.juny.spacestory.service;

//@ExtendWith(MockitoExtension.class)
//public class ReviewServiceTest {
//
//    private final ReviewMapper mapper = Mappers.getMapper(ReviewMapper.class);
//    @Mock
//    ReviewRepository reviewRepository;
//
//    @Mock
//    UserRepository userRepository;
//    @InjectMocks
//    private ReviewService reviewService;
//    private User user;
//    @BeforeEach
//    void setUp() throws NoSuchFieldException, IllegalAccessException {
//        ReflectionTestUtils.setField(reviewService, "mapper", mapper);
//        user = new User("user1", "user1@gmail.com", "1234", null);
//        Field idField = User.class.getDeclaredField("id");
//        idField.setAccessible(true);
//        idField.set(user, UUID.randomUUID());
//    }
//
//    @DisplayName("리뷰를 등록한다.")
//    @Test
//    void createReview() {
//        //given
//        ReqCreateReview req = new ReqCreateReview("comment1", 4.5, user.getId());
//        Review expectedReview = new Review("comment1", 4.5, user.getId(), false);
//        ResReview expected = mapper.ReviewToResponseReview(expectedReview);
//
//        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(user));
//        when(reviewRepository.save(any(Review.class))).thenReturn(expectedReview);
//
//        //when
//        ResReview review = reviewService.create(req);
//
//        //then
//        assertThat(review).isNotNull();
//        assertThat(review).usingRecursiveComparison().isEqualTo(expected);
//    }
//
//    @DisplayName("리뷰를 수정한다.")
//    @Test
//    void updateReview() {
//        //given
//        RequestUpdateReview req = new RequestUpdateReview("comment1", 4.5);
//        Review expectedReview = new Review("comment1", 4.5, user.getId(), false);
//        ResReview expected = mapper.ReviewToResponseReview(expectedReview);
//
//        Review before = new Review("origin", 4.5, user.getId(), false);
//
//        when(reviewRepository.findById(any())).thenReturn(Optional.of(before));
//        when(reviewRepository.save(before)).thenReturn(expectedReview);
//
//        //when
//        ResReview updatedReview = reviewService.update(any(), req);
//
//        //then
//        assertThat(updatedReview).isNotNull();
//        assertThat(updatedReview).usingRecursiveComparison().isEqualTo(expected);
//    }
//
//    @DisplayName("리뷰를 삭제한다.")
//    @Test
//    void deleteReview() {
//        //given
//        Review expectedReview = new Review("comment1", 4.5, user.getId(), false);
//        ResReview expected = mapper.ReviewToResponseReview(expectedReview);
//
//        when(reviewRepository.findById(any())).thenReturn(Optional.of(expectedReview));
//
//        //when
//        reviewService.delete(any());
//
//        //then
//        verify(reviewRepository).findById(any());
//        assertThat(expectedReview.getIsDeleted()).isEqualTo(true);
//    }
//}

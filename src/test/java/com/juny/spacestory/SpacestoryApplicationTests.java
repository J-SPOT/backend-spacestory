package com.juny.spacestory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SpacestoryApplicationTests {

	@DisplayName("CRUD 시나리오")
	// 사용자 1, 2를 생성한다.
	// 호스트가 부동산을 생성한다.
	// 해당 부동산에 공간 1개를 만든다.
	// 유저1이 공간1에 대한 예약을 2개 만든다.
	// 유저2가 공간1에서 유저1이 유저1이 예약한 시간에 예약을 하지 못하는 걸 보여준다.
	// 유저1이 공간 예약 하나를 취소한다.
	// 유저2가 그 공간을 예약한다.
	// 유저1이 이미 예약한 공간을 삭제한다.
	// 유저2가 그 공간을 예약한다.
	@Test
	void reservatio() {
	    //given

	    //when

	    //then
	}
	@Test
	void contextLoads() {
	}
}

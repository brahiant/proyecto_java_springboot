package com.proyecto.spring_back;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import com.proyecto.spring_back.mapper.UserRequestMapper;
import com.proyecto.spring_back.mapper.UserSaveRequestMapper;
import com.proyecto.spring_back.mapper.LoginRequestMapper;

@SpringBootTest
@ActiveProfiles("test")
class SpringBackApplicationTests {

	@MockBean(name = "userRequestMapperImpl")
	private UserRequestMapper userRequestMapper;

	@MockBean(name = "userSaveRequestMapperImpl")
	private UserSaveRequestMapper userSaveRequestMapper;

	@MockBean(name = "loginRequestMapperImpl")
	private LoginRequestMapper loginRequestMapper;

	@Test
	void contextLoads() {
	}

}

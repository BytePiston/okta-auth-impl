package com.cactus.resource.server.controller;

import com.cactus.resource.server.entity.User;
import com.cactus.resource.server.response.UserResponse;
import com.cactus.resource.server.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/v1")
public class UserController {

	private IUserService userService;

	@Autowired
	public UserController(IUserService userService) {
		this.userService = userService;
	}

	@GetMapping("/")
	public UserResponse fetchLoggedInUser(@AuthenticationPrincipal Jwt jwt) {
		Optional<User> userOptional = userService.fetchUserByEmail(jwt.getSubject());
		if (userOptional.isPresent()) {
			User user = userOptional.get();
			return UserResponse.builder()
				.firstName(user.getFirstName())
				.lastName(user.getLastName())
				.email(user.getEmail())
				.role(user.getRole())
				.build();
		}
		return null;
	}

	@GetMapping("/users")
	public List<UserResponse> fetchAllUsers() {
		List<User> userList = userService.fetchAllUsers();
		if (userList != null && !userList.isEmpty()) {
			List<UserResponse> userResponseList = new ArrayList<>();
			for (User user : userList) {
				UserResponse userResponse = UserResponse.builder()
					.firstName(user.getFirstName())
					.lastName(user.getLastName())
					.email(user.getEmail())
					.role(user.getRole())
					.build();
				userResponseList.add(userResponse);
			}
			return userResponseList;
		}
		return new ArrayList<>();
	}

}

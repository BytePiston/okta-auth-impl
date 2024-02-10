package com.cactus.resource.server.service;

import com.cactus.resource.server.entity.User;
import com.cactus.resource.server.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserServiceImpl implements IUserService {

	private final UserRepository userRepository;

	@Autowired
	public UserServiceImpl(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public Optional<User> fetchUserByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	@Override
	public List<User> fetchAllUsers() {
		return userRepository.findAll();
	}

}

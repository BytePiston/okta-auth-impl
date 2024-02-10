package com.cactus.resource.server.service;

import com.cactus.resource.server.entity.User;

import java.util.List;
import java.util.Optional;

public interface IUserService {

	Optional<User> fetchUserByEmail(String email);

	List<User> fetchAllUsers();

}

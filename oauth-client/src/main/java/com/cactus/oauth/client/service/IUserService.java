package com.cactus.oauth.client.service;

import com.cactus.oauth.client.exception.ResourceNotFoundException;
import com.cactus.oauth.client.entity.RegistrationToken;
import com.cactus.oauth.client.entity.ResetPasswordToken;
import com.cactus.oauth.client.entity.User;
import com.cactus.oauth.client.model.ChangePasswordModel;
import com.cactus.oauth.client.model.UserModel;

import java.util.List;
import java.util.Optional;

public interface IUserService {

	User registerUser(UserModel userModel);

	void persistVerificationToken(String token, User user);

	String validateRegistrationToken(String token);

	Optional<RegistrationToken> fetchVerificationToken(String oldToken);

	void deleteVerificationToken(RegistrationToken registrationTokenObj);

	Optional<User> fetchUserByEmail(String email);

	void persistResetPasswordToken(String token, User user);

	String validateResetPasswordToken(String token);

	void updatePassword(String email, String newPassword) throws ResourceNotFoundException;

	Optional<ResetPasswordToken> fetchResetPasswordToken(String oldToken);

	void deleteResetPasswordToken(ResetPasswordToken passwordToken);

	boolean isValidateUserAndOldPassword(ChangePasswordModel changePasswordModel);

	List<User> fetchAllUsers();

}

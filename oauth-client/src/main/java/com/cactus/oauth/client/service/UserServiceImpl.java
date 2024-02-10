package com.cactus.oauth.client.service;

import com.cactus.oauth.client.exception.ResourceNotFoundException;
import com.cactus.oauth.client.repository.ResetPasswordTokenRepository;
import com.cactus.oauth.client.repository.UserRepository;
import com.cactus.oauth.client.repository.VerificationTokenRepository;
import com.cactus.oauth.client.entity.RegistrationToken;
import com.cactus.oauth.client.entity.ResetPasswordToken;
import com.cactus.oauth.client.entity.User;
import com.cactus.oauth.client.model.ChangePasswordModel;
import com.cactus.oauth.client.model.UserModel;
import com.okta.sdk.resource.api.UserApi;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.user.UserBuilder;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import static com.cactus.oauth.client.utils.Constants.*;

@Service
@Slf4j
public class UserServiceImpl implements IUserService {

	private final UserRepository userRepository;

	private final PasswordEncoder passwordEncoder;

	private final VerificationTokenRepository verificationTokenRepository;

	private final ResetPasswordTokenRepository resetPasswordTokenRepository;

	private final ApiClient apiClient;

	@Autowired
	public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder,
			VerificationTokenRepository verificationTokenRepository,
			ResetPasswordTokenRepository resetPasswordTokenRepository, ApiClient apiClient) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.verificationTokenRepository = verificationTokenRepository;
		this.resetPasswordTokenRepository = resetPasswordTokenRepository;
		this.apiClient = apiClient;
	}

	@Override
	public User registerUser(UserModel userModel) {
		User user = User.builder()
			.firstName(userModel.getFirstName())
			.lastName(userModel.getLastName())
			.email(userModel.getEmail())
			.role(userModel.getRole())
			.password(passwordEncoder.encode(userModel.getPassword()))
			.build();
		userRepository.save(user);
		return user;
	}

	@Override
	public void persistVerificationToken(String token, User user) {
		RegistrationToken registrationToken = new RegistrationToken(token, user);
		verificationTokenRepository.save(registrationToken);
	}

	@Override
	@Transactional
	@Modifying
	public String validateRegistrationToken(String token) {
		Optional<RegistrationToken> verificationToken = verificationTokenRepository.findByToken(token);
		if (verificationToken.isPresent()) {
			RegistrationToken registrationTokenObj = verificationToken.get();
			if (registrationTokenObj.getExpirationTime().compareTo(Calendar.getInstance().getTime()) >= 0) {
				User user = registrationTokenObj.getUser();

				UserApi userApi = new UserApi(apiClient);
				userApi.activateUser(user.getEmail(), false);

				user.setEnabled(true);
				userRepository.save(user);
				verificationTokenRepository.delete(registrationTokenObj);
				return SUCCESS;
			}
			return EXPIRED;
		}
		return INVALID;
	}

	@Override
	public Optional<RegistrationToken> fetchVerificationToken(String oldToken) {
		return verificationTokenRepository.findByToken(oldToken);
	}

	@Override
	public void deleteVerificationToken(RegistrationToken registrationTokenObj) {
		verificationTokenRepository.delete(registrationTokenObj);
	}

	@Override
	public Optional<User> fetchUserByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	@Override
	public void persistResetPasswordToken(String token, User user) {
		ResetPasswordToken resetPasswordToken = new ResetPasswordToken(token, user);
		resetPasswordTokenRepository.save(resetPasswordToken);
	}

	@Override
	public String validateResetPasswordToken(String token) {
		Optional<ResetPasswordToken> resetPasswordTokenOptional = resetPasswordTokenRepository.findByToken(token);
		if (resetPasswordTokenOptional.isPresent()) {
			ResetPasswordToken resetPasswordToken = resetPasswordTokenOptional.get();
			if (resetPasswordToken.getExpirationTime().getTime() - Calendar.getInstance().getTimeInMillis() >= 0) {
				return SUCCESS;
			}
			return EXPIRED;
		}
		else
			return INVALID;
	}

	@Override
	public void updatePassword(String email, String newPassword) throws ResourceNotFoundException {
		Optional<User> userOptional = fetchUserByEmail(email);
		if (userOptional.isPresent()) {
			User user = userOptional.get();
			user.setPassword(passwordEncoder.encode(newPassword));
			userRepository.save(user);
		}
		else {
			log.error("User Not Found With Email: " + email);
			throw new ResourceNotFoundException("USER NOT_FOUND!!");
		}
	}

	@Override
	public Optional<ResetPasswordToken> fetchResetPasswordToken(String oldToken) {
		return resetPasswordTokenRepository.findByToken(oldToken);
	}

	@Override
	public void deleteResetPasswordToken(ResetPasswordToken passwordToken) {
		resetPasswordTokenRepository.delete(passwordToken);
	}

	@Override
	public boolean isValidateUserAndOldPassword(ChangePasswordModel changePasswordModel) {
		Optional<User> userOptional = fetchUserByEmail(changePasswordModel.getEmail());
		return userOptional.isPresent()
				&& passwordEncoder.matches(changePasswordModel.getOldPassword(), userOptional.get().getPassword());
	}

	@Override
	public List<User> fetchAllUsers() {
		return userRepository.findAll();
	}

}

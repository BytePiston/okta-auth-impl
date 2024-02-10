package com.cactus.oauth.client.controller;

import com.cactus.oauth.client.entity.RegistrationToken;
import com.cactus.oauth.client.entity.ResetPasswordToken;
import com.cactus.oauth.client.entity.User;
import com.cactus.oauth.client.event.RegistrationSuccessEvent;
import com.cactus.oauth.client.event.ResetPasswordEvent;
import com.cactus.oauth.client.exception.ResourceNotFoundException;
import com.cactus.oauth.client.model.ChangePasswordModel;
import com.cactus.oauth.client.model.ResetPasswordModel;
import com.cactus.oauth.client.model.UserModel;
import com.cactus.oauth.client.response.RegistrationTokenResponse;
import com.cactus.oauth.client.response.ResetPasswordResponse;
import com.cactus.oauth.client.response.UserRegistrationResponse;
import com.cactus.oauth.client.response.UserResponse;
import com.cactus.oauth.client.service.IUserService;
import com.cactus.oauth.client.utils.Utils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.okta.sdk.authc.credentials.TokenClientCredentials;
import com.okta.sdk.client.Clients;
import com.okta.sdk.resource.api.UserApi;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.user.UserBuilder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static com.cactus.oauth.client.utils.Constants.*;
import static org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction.oauth2AuthorizedClient;

@RestController
@RequestMapping("api/v1/user")
public class UserController {

	private final IUserService userService;

	private final ApplicationEventPublisher eventPublisher;

	private final WebClient webClient;

	private final ApiClient apiClient;

	// Fetching Resource-server URL from application.yml;
	@Value("${resource-server.api-url-value}")
	public String RESOURCE_SERVER_API_URL;

	@Autowired
	public UserController(IUserService userService, ApplicationEventPublisher eventPublisher, WebClient webClient,
			ApiClient apiClient) {
		this.userService = userService;
		this.eventPublisher = eventPublisher;
		this.webClient = webClient;
		this.apiClient = apiClient;
	}

	@PostMapping("/register")
	public ResponseEntity<UserRegistrationResponse> registerUser(@Valid @RequestBody UserModel userModel,
			final HttpServletRequest request) {

		UserApi userApi = new UserApi(apiClient);
		try {
			UserBuilder.instance()
				.setActive(false)
				.setFirstName(userModel.getFirstName())
				.setLastName(userModel.getLastName())
				.setEmail(userModel.getEmail())
				.setPassword(userModel.getPassword().toCharArray())
				.buildAndCreate(userApi);
		}
		catch (ApiException e) {
			UserRegistrationResponse response = UserRegistrationResponse.builder()
				.status(ERROR)
				.message(e.getMessage())
				.build();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}

		User user = userService.registerUser(userModel);
		RegistrationSuccessEvent event = new RegistrationSuccessEvent(user, Utils.getApplicationUrl(request));
		eventPublisher.publishEvent(event);
		CompletableFuture<String> verificationUrl = event.getVerificationUrlFuture();
		UserRegistrationResponse response = UserRegistrationResponse.builder()
			.firstName(userModel.getFirstName())
			.lastName(userModel.getLastName())
			.email(userModel.getEmail())
			.role(userModel.getRole())
			.verificationUrl(verificationUrl.join())
			.status(SUCCESS)
			.build();
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@GetMapping("/verifyToken")
	public ResponseEntity<RegistrationTokenResponse> validateTokenUrl(@RequestParam("token") String token,
			HttpServletRequest request) {

		String responseString = userService.validateRegistrationToken(token);
		if (responseString.equals(SUCCESS)) {
			RegistrationTokenResponse response = RegistrationTokenResponse.builder()
				.status(SUCCESS)
				.message("Token Successfully Verified!!")
				.build();
			return ResponseEntity.status(HttpStatus.OK).body(response);
		}
		else if (responseString.equals(INVALID)) {
			RegistrationTokenResponse response = RegistrationTokenResponse.builder()
				.status(INVALID)
				.message("Invalid Token, please verify URL!!")
				.build();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
		else {
			String url = Utils.getApplicationUrl(request) + FORWARD_SLASH + USER + FORWARD_SLASH
					+ RESEND_VERIFY_TOKEN_ENDPOINT + token;
			RegistrationTokenResponse response = RegistrationTokenResponse.builder()
				.status(EXPIRED)
				.message("Token Expired, Please follow link to generate New Token!!")
				.tokenUrl(url)
				.build();
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
		}
	}

	@PostMapping("/resendVerificationToken")
	public ResponseEntity<RegistrationTokenResponse> resendVerificationToken(@RequestParam("token") String oldToken,
			HttpServletRequest request) {

		Optional<RegistrationToken> verificationToken = userService.fetchVerificationToken(oldToken);
		if (verificationToken.isPresent()) {
			RegistrationToken registrationTokenObj = verificationToken.get();
			User user = registrationTokenObj.getUser();
			userService.deleteVerificationToken(registrationTokenObj);
			RegistrationSuccessEvent event = new RegistrationSuccessEvent(user, Utils.getApplicationUrl(request));
			eventPublisher.publishEvent(event);
			CompletableFuture<String> verificationUrl = event.getVerificationUrlFuture();
			RegistrationTokenResponse response = RegistrationTokenResponse.builder()
				.status(SUCCESS)
				.message("New Token Successfully Generated!!")
				.tokenUrl(verificationUrl.join())
				.build();
			return ResponseEntity.status(HttpStatus.CREATED).body(response);
		}

		RegistrationTokenResponse response = RegistrationTokenResponse.builder()
			.status(INVALID)
			.message("Invalid Token, please verify URL!!")
			.build();
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	}

	@PostMapping("/resetPassword")
	public ResponseEntity<ResetPasswordResponse> resetPassword(@NotNull @RequestParam("email") String email,
			HttpServletRequest request) {
		Optional<User> userOptional = userService.fetchUserByEmail(email);
		if (userOptional.isPresent()) {
			User user = userOptional.get();
			ResetPasswordEvent event = new ResetPasswordEvent(user, Utils.getApplicationUrl(request));
			eventPublisher.publishEvent(event);
			CompletableFuture<String> resetUrlFuture = event.getResetPasswordUrlFuture();
			ResetPasswordResponse response = ResetPasswordResponse.builder()
				.status(SUCCESS)
				.message("New Url Successfully Generated!!")
				.tokenUrl(resetUrlFuture.join())
				.build();

			/*
			 * TODO: Block User until new password is updated in Okta;
			 */

			return ResponseEntity.status(HttpStatus.CREATED).body(response);
		}

		ResetPasswordResponse response = ResetPasswordResponse.builder()
			.status(INVALID)
			.message("Invalid Request, please verify Email!!")
			.build();
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	}

	@PostMapping("/updatePassword")
	public ResponseEntity<ResetPasswordResponse> updatePassword(
			@Valid @RequestBody ResetPasswordModel resetPasswordModel, @RequestParam("token") String token,
			HttpServletRequest request) throws ResourceNotFoundException {
		String responseString = userService.validateResetPasswordToken(token);
		if (responseString.equals(SUCCESS)) {

			/*
			 * TODO: Unblock User and Update password in Okta;
			 */

			userService.updatePassword(resetPasswordModel.getEmail(), resetPasswordModel.getNewPassword());
			ResetPasswordResponse response = ResetPasswordResponse.builder()
				.status(SUCCESS)
				.message("Successfully Updated Password!!")
				.build();
			return ResponseEntity.status(HttpStatus.OK).body(response);
		}
		else if (responseString.equals(INVALID)) {
			ResetPasswordResponse response = ResetPasswordResponse.builder()
				.status(INVALID)
				.message("Invalid Token, please verify URL!!")
				.build();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
		else {
			String url = Utils.getApplicationUrl(request) + FORWARD_SLASH + USER + FORWARD_SLASH
					+ RESEND_RESET_PASSWORD_TOKEN_ENDPOINT + token;
			ResetPasswordResponse response = ResetPasswordResponse.builder()
				.status(EXPIRED)
				.message("Token Expired, Please follow link to generate New Token!!")
				.tokenUrl(url)
				.build();
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
		}
	}

	@PostMapping("/resendResetPasswordToken")
	public ResponseEntity<ResetPasswordResponse> resendResetPasswordToken(@RequestParam("token") String oldToken,
			HttpServletRequest request) {
		Optional<ResetPasswordToken> passwordTokenOptional = userService.fetchResetPasswordToken(oldToken);
		if (passwordTokenOptional.isPresent()) {
			ResetPasswordToken passwordToken = passwordTokenOptional.get();
			User user = passwordToken.getUser();
			userService.deleteResetPasswordToken(passwordToken);
			ResetPasswordEvent event = new ResetPasswordEvent(user, Utils.getApplicationUrl(request));
			eventPublisher.publishEvent(event);
			CompletableFuture<String> resetPasswordUrl = event.getResetPasswordUrlFuture();
			ResetPasswordResponse response = ResetPasswordResponse.builder()
				.status(SUCCESS)
				.message("New Token Successfully Generated!!")
				.tokenUrl(resetPasswordUrl.join())
				.build();
			return ResponseEntity.status(HttpStatus.CREATED).body(response);
		}
		else {
			ResetPasswordResponse response = ResetPasswordResponse.builder()
				.status(INVALID)
				.message("Invalid Token, please verify URL!!")
				.build();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
	}

	@PostMapping("/changePassword")
	public ResponseEntity<ResetPasswordResponse> changePassword(
			@Valid @RequestBody ChangePasswordModel changePasswordModel, HttpServletRequest request)
			throws ResourceNotFoundException {
		if (userService.isValidateUserAndOldPassword(changePasswordModel)) {
			userService.updatePassword(changePasswordModel.getEmail(), changePasswordModel.getNewPassword());
			ResetPasswordResponse response = ResetPasswordResponse.builder()
				.status(SUCCESS)
				.message("Password Updated Successfully!!")
				.build();
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
		}
		ResetPasswordResponse response = ResetPasswordResponse.builder()
			.status(INVALID)
			.message("Invalid Password Update Request, Please Verify!!")
			.build();
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	}

	// Below 2 endpoint are fetched from Resource Server using Webclient
	@GetMapping("/")
	public UserResponse fetchLoggedInUser(
			@RegisteredOAuth2AuthorizedClient("okta") OAuth2AuthorizedClient authorizedClient) {
		return this.webClient.get()
			.uri(RESOURCE_SERVER_API_URL + "/")
			.attributes(oauth2AuthorizedClient(authorizedClient))
			.retrieve()
			.bodyToMono(UserResponse.class)
			.block();
	}

	@GetMapping("/viewAllUsers")
	public List<UserResponse> fetchAllUsers(
			@RegisteredOAuth2AuthorizedClient("okta") OAuth2AuthorizedClient authorizedClient) {
		return this.webClient.get()
			.uri(RESOURCE_SERVER_API_URL + "/users")
			.attributes(oauth2AuthorizedClient(authorizedClient))
			.retrieve()
			.bodyToMono(new ParameterizedTypeReference<List<UserResponse>>() {
			})
			.block();
	}

}

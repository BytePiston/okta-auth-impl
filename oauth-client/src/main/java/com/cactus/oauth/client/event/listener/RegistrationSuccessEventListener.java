package com.cactus.oauth.client.event.listener;

import com.cactus.oauth.client.entity.User;
import com.cactus.oauth.client.service.IUserService;
import com.cactus.oauth.client.utils.Constants;
import com.cactus.oauth.client.event.RegistrationSuccessEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class RegistrationSuccessEventListener implements ApplicationListener<RegistrationSuccessEvent> {

	private final IUserService userService;

	@Autowired
	public RegistrationSuccessEventListener(IUserService userService) {
		this.userService = userService;
	}

	@Override
	public void onApplicationEvent(RegistrationSuccessEvent event) {
		User user = event.getUser();
		String token = UUID.randomUUID().toString();
		userService.persistVerificationToken(token, user);
		String verificationUrl = event.getApplicationUrl() + Constants.FORWARD_SLASH + Constants.USER
				+ Constants.FORWARD_SLASH + Constants.VERIFY_TOKEN_ENDPOINT + token;
		log.info("Follow the link to verify your account!! : " + verificationUrl);
		event.getVerificationUrlFuture().complete(verificationUrl);
	}

}

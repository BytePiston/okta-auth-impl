package com.cactus.oauth.client.event.listener;

import com.cactus.oauth.client.entity.User;
import com.cactus.oauth.client.event.ResetPasswordEvent;
import com.cactus.oauth.client.service.IUserService;
import com.cactus.oauth.client.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class ResetPasswordEventListener implements ApplicationListener<ResetPasswordEvent> {

	private final IUserService userService;

	@Autowired
	public ResetPasswordEventListener(IUserService userService) {
		this.userService = userService;
	}

	@Override
	public void onApplicationEvent(ResetPasswordEvent event) {
		User user = event.getUser();
		String token = UUID.randomUUID().toString();
		userService.persistResetPasswordToken(token, user);
		String resetPasswordUrl = event.getApplicationUrl() + Constants.FORWARD_SLASH + Constants.USER
				+ Constants.FORWARD_SLASH + Constants.UPDATE_PASSWORD_ENDPOINT + token;
		log.info("Follow the link to reset your password!! : " + resetPasswordUrl);
		event.getResetPasswordUrlFuture().complete(resetPasswordUrl);
	}

}

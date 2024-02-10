package com.cactus.oauth.client.event;

import com.cactus.oauth.client.entity.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

import java.util.concurrent.CompletableFuture;

@Getter
@Setter
public class RegistrationSuccessEvent extends ApplicationEvent {

	private User user;

	private String applicationUrl;

	private final CompletableFuture<String> verificationUrlFuture;

	public RegistrationSuccessEvent(User user, String applicationUrl) {
		super(user);
		this.user = user;
		this.applicationUrl = applicationUrl;
		this.verificationUrlFuture = new CompletableFuture<>();
	}

}

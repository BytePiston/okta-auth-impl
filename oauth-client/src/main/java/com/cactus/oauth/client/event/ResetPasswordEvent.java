package com.cactus.oauth.client.event;

import com.cactus.oauth.client.entity.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

import java.util.concurrent.CompletableFuture;

@Getter
@Setter
public class ResetPasswordEvent extends ApplicationEvent {

	private User user;

	private String applicationUrl;

	private final CompletableFuture<String> resetPasswordUrlFuture;

	public ResetPasswordEvent(User user, String applicationUrl) {
		super(user);
		this.user = user;
		this.applicationUrl = applicationUrl;
		this.resetPasswordUrlFuture = new CompletableFuture<>();
	}

}

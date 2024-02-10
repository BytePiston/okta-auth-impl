package com.cactus.oauth.client.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserRegistrationResponse {

	private String firstName;

	private String lastName;

	private String email;

	private String role;

	private String verificationUrl;

	private String status;

	private String message;

}

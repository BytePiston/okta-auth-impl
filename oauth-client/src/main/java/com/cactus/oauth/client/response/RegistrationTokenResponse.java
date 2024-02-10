package com.cactus.oauth.client.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RegistrationTokenResponse {

	private String status;

	private String message;

	private String tokenUrl;

}

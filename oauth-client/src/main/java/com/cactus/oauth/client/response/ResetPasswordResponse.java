package com.cactus.oauth.client.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResetPasswordResponse {

	private String status;

	private String message;

	private String tokenUrl;

}

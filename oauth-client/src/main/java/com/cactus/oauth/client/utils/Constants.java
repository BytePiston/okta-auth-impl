package com.cactus.oauth.client.utils;

public abstract class Constants {

	public static final int EXPIRATION_TIME_IN_MINUTES = 10;

	public static final String VERIFY_TOKEN_ENDPOINT = "verifyToken?token=";

	public static final String UPDATE_PASSWORD_ENDPOINT = "updatePassword?token=";

	public static final String RESEND_VERIFY_TOKEN_ENDPOINT = "resendVerificationToken?token=";

	public static final String RESEND_RESET_PASSWORD_TOKEN_ENDPOINT = "resendResetPasswordToken?token=";

	public static final String HTTP = "http://";

	public static final String FORWARD_SLASH = "/";

	public static final String BASE_API_PATH = "/api/v1";

	public static final String USER = "user";

	public static final String SUCCESS = "SUCCESS";

	public static final String EXPIRED = "EXPIRED";

	public static final String INVALID = "INVALID";

	public static final String ERROR = "ERROR";

	public static final String API_CLIENT_AUTHORIZATION_CODE = "api-client-authorization-code";

}

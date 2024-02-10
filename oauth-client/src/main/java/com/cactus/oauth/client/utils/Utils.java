package com.cactus.oauth.client.utils;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Calendar;
import java.util.Date;

import static com.cactus.oauth.client.utils.Constants.BASE_API_PATH;
import static com.cactus.oauth.client.utils.Constants.HTTP;

public class Utils {

	public static String getApplicationUrl(HttpServletRequest request) {
		return HTTP + request.getServerName() + ":" + request.getServerPort() + request.getContextPath()
				+ BASE_API_PATH;
	}

	public static Date calculateExpirationTime() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(new Date().getTime());
		calendar.add(Calendar.MINUTE, Constants.EXPIRATION_TIME_IN_MINUTES);
		return calendar.getTime();
	}

}

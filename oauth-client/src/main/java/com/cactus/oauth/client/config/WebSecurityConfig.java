package com.cactus.oauth.client.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.SecurityFilterChain;

import java.util.function.Consumer;

@EnableWebSecurity
@Configuration
public class WebSecurityConfig {

	private static final String[] WHITE_LIST_GET_URLS = { "/api/v1/hello", "/api/v1/user/verifyToken*" };

	private static final String[] WHITE_LIST_POST_URLS = { "/api/v1/user/register",
			"/api/v1/user/resendVerificationToken*" };

	@Autowired
	private ClientRegistrationRepository clientRegistrationRepository;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(11);
	}

	@Bean
	public SecurityFilterChain webSecurityFilterChainClient(HttpSecurity httpSecurity) throws Exception {
		httpSecurity.cors(CorsConfigurer::disable).csrf(CsrfConfigurer::disable).authorizeHttpRequests(req -> {
			req.requestMatchers(HttpMethod.GET, WHITE_LIST_GET_URLS).permitAll();
			req.requestMatchers(HttpMethod.POST, WHITE_LIST_POST_URLS).permitAll();
			req.anyRequest().authenticated();
		})
			.oauth2Login(oauth2login -> oauth2login.loginPage("/oauth2/authorization/okta")
				.authorizationEndpoint(authorization -> authorization
					.authorizationRequestResolver(authorizationRequestResolver(this.clientRegistrationRepository))))
			.oauth2Client(Customizer.withDefaults());
		return httpSecurity.build();
	}

	private OAuth2AuthorizationRequestResolver authorizationRequestResolver(
			ClientRegistrationRepository clientRegistrationRepository) {

		DefaultOAuth2AuthorizationRequestResolver authorizationRequestResolver = new DefaultOAuth2AuthorizationRequestResolver(
				clientRegistrationRepository, "/oauth2/authorization/okta");
		authorizationRequestResolver.setAuthorizationRequestCustomizer(authorizationRequestCustomizer());

		return authorizationRequestResolver;
	}

	private Consumer<OAuth2AuthorizationRequest.Builder> authorizationRequestCustomizer() {
		return customizer -> customizer.additionalParameters(params -> params.put("prompt", "consent"));
	}

}

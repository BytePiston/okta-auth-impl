package com.cactus.oauth.client.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordModel {

	@NotBlank(message = "Email Address Can Not Be Blank!")
	@NotNull(message = "Email Address Can Not Be Empty!")
	@Email(regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}", flags = Pattern.Flag.CASE_INSENSITIVE)
	private String email;

	@NotBlank(message = "Old Password Can Not Be Blank!")
	@NotNull(message = "Old Password Can Not Be Empty!")
	private String oldPassword;

	@NotBlank(message = "New Password Can Not Be Blank!")
	@NotNull(message = "New Password Can Not Be Empty!")
	private String newPassword;

}

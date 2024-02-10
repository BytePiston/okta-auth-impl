package com.cactus.oauth.client.model;

import com.cactus.oauth.client.validator.ValidateEnum;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserModel {

	@NotBlank(message = "First Name Can Not Be Blank!")
	private String firstName;

	@NotBlank(message = "First Name Can Not Be Blank!")
	private String lastName;

	@NotBlank(message = "Email Address Can Not Be Blank!")
	@Email(regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}", flags = Pattern.Flag.CASE_INSENSITIVE)
	private String email;

	@NotBlank(message = "Password Can Not Be Blank!")
	@Size(min = 8, max = 30)
	@Pattern(message = "Password Not Meeting Minimum Requirements", regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,30}$")
	private String password;

	private String matchingPassword;

	@NotBlank(message = "Role Can Not Be Blank!")
	@ValidateEnum(acceptedValues = UserRole.class, message = "Invalid User Role")
	private String role;

}

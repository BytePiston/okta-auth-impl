package com.cactus.oauth.client.entity;

import com.cactus.oauth.client.utils.Utils;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordToken {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String id;

	private String token;

	@Temporal(TemporalType.TIMESTAMP)
	private Date expirationTime;

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
	private User user;

	public ResetPasswordToken(String token) {
		super();
		this.token = token;
		this.expirationTime = Utils.calculateExpirationTime();
	}

	public ResetPasswordToken(String token, User user) {
		super();
		this.token = token;
		this.user = user;
		this.expirationTime = Utils.calculateExpirationTime();
	}

}

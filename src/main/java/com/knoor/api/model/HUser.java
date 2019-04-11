package com.knoor.api.model;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author ahajri
 */
@Getter
@Setter
public class HUser {

	@Email(message = "Email not valid")
	private String email;

	@NotNull(message = "Password required")
	private String password;

	private List<String> roles = new ArrayList<>();

	private String firstName, lastName, gender;

	private boolean actif;

	private String token;

	public HUser() {

	}

	public HUser(String email, String password) {
		super();
		this.email = email;
		this.password = password;
	}

}

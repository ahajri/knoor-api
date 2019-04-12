package com.knoor.api.security;


import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoginRequest {
    @NotBlank
    @Email(message="Bad email format !")
    private String email;

    @NotBlank
    private String password;

	public LoginRequest(@NotBlank String email, @NotBlank String password) {
		super();
		this.email = email;
		this.password = password;
	}

	
    


}

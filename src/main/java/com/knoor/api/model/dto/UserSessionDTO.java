package com.knoor.api.model.dto;

import java.time.LocalDateTime;

import lombok.ToString;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@ToString
@NoArgsConstructor
public class UserSessionDTO {
	
	private String firstName,lastName,gender,email,token;
	private LocalDateTime sessionStartDateTime;
	boolean actif;

}

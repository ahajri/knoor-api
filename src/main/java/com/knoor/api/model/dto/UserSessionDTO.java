package com.knoor.api.model.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
@NoArgsConstructor
public class UserSessionDTO {
	
	private String firstName,lastName,gender,email,token;
	private LocalDateTime sessionStartDateTime;
	boolean actif;

}

package com.knoor.api.model;

import javax.validation.constraints.Email;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Document(collection="users")
public class UserModel {
	
	
	@Id
	private ObjectId _id;
	
	@Email
	private String email;
	
	private String password,firstName,lastName,gender;
	
	private boolean actif;
	
	
	 

}

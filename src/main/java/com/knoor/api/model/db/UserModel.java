package com.knoor.api.model.db;

import java.util.List;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

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
	
	@NotNull
	private String password,firstName,lastName,gender;
	
	private boolean actif;
	
	private List<String> roles;
	
	
	 

}

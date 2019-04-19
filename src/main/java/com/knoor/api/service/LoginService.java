package com.knoor.api.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.knoor.api.enums.ErrorMessageEnum;
import com.knoor.api.exception.BusinessException;
import com.knoor.api.model.db.UserModel;
import com.knoor.api.model.dto.UserSessionDTO;
import com.knoor.api.security.JwtTokenProvider;

@Service
public class LoginService {

	private static final Logger LOG = LoggerFactory.getLogger(LoginService.class);
	
	MongoTemplate mongoTemplate;
	PasswordEncoder passwordEncoder;
	JwtTokenProvider jwtTokenProvider;

	@Autowired
	public LoginService(MongoTemplate mongoTemplate, PasswordEncoder passwordEncoder,JwtTokenProvider jwtTokenProvider) {
		this.mongoTemplate = mongoTemplate;
		this.passwordEncoder = passwordEncoder;
		this.jwtTokenProvider=jwtTokenProvider;
	}

	public UserModel findUserByEmail(String email) {
		Query query = new Query();
		query.addCriteria(Criteria.where("email").is(email));
		return (UserModel) mongoTemplate.findOne(query, UserModel.class);
	}

	/**
	 * 
	 * @param email
	 * @param password
	 * @return
	 * @throws BusinessException
	 */
	public UserSessionDTO login(String email, String password) throws BusinessException {
		UserModel user = this.findUserByEmail(email);
		UserSessionDTO result = new UserSessionDTO();
		if(user == null) {
			throw new BusinessException(ErrorMessageEnum.USER_NOT_FOUND_FOR_EMAIL.getMessage(email),HttpStatus.NOT_FOUND);
		}
		if (passwordEncoder.matches(password, user.getPassword())) {
			UsernamePasswordAuthenticationToken jwtAuth = new UsernamePasswordAuthenticationToken(email, password);
			String token = jwtTokenProvider.generateToken(jwtAuth);
			result.setToken(token);
			result.setFirstName(user.getFirstName());
			result.setLastName(user.getLastName());
			result.setGender(user.getGender());
			result.setActif(user.isActif());
			result.setEmail(email);
			return result;
		} else {
			LOG.error(ErrorMessageEnum.WRONG_PASSWORD.getMessage());
			throw new BusinessException(ErrorMessageEnum.WRONG_PASSWORD.getMessage());
		}

	}

}

package com.knoor.api.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.knoor.api.beans.QueryParam;
import com.knoor.api.enums.ErrorMessageEnum;
import com.knoor.api.enums.OperatorEnum;
import com.knoor.api.exception.BusinessException;
import com.knoor.api.exception.RestException;
import com.knoor.api.model.HUser;
import com.knoor.api.model.db.UserModel;
import com.knoor.api.model.dto.UserSessionDTO;
import com.knoor.api.security.JwtTokenProvider;
import com.knoor.api.security.LoginRequest;
import com.knoor.api.service.CloudMongoService;
import com.knoor.api.service.LoginService;

@RestController
public class LoginController {

	@Value("${application.key}")
	protected String applicationKey;

	@Value("${access.token}")
	protected String accessToken;

	@Autowired
	private CloudMongoService cloudMongoService;
	
	@Autowired
	private LoginService loginService;

	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private JwtTokenProvider jwtTokenProvider;

	private static final Gson gson = new Gson();

	private static Logger LOG = LoggerFactory.getLogger(LoginController.class);

	private static final String USER_COLLECTION_NAME = "users";
	
	@PostMapping(path = "/auth")
	public ResponseEntity<UserSessionDTO> auth(@Valid @RequestBody LoginRequest loginRequest) throws RestException {
		try {
			return ResponseEntity.ok(loginService.login(loginRequest.getEmail(),loginRequest.getPassword()));
		} catch (BusinessException e) {
			throw new RestException(e.getMessage(),e,e.getHttpStatus());
		}
		
	}

		

	@PostMapping(path = "/login")
	public ResponseEntity<HUser> login(@Valid @RequestBody LoginRequest loginRequest) throws RestException {

		HUser result = new HUser();

		QueryParam[] qps = new QueryParam[1];

		qps[0] = new QueryParam("email", OperatorEnum.EQ.name(), loginRequest.getEmail());
		List<HUser> foundUsers = null;
		try {
			foundUsers = cloudMongoService.search(USER_COLLECTION_NAME, qps).stream()
					.map(d -> gson.fromJson(gson.toJson(d), HUser.class)).collect(Collectors.toList());
		} catch (BusinessException e) {
			LOG.error(e.getMessage(), e);
			throw new RestException(ErrorMessageEnum.USER_NOT_FOUND_FOR_EMAIL.getMessage(loginRequest.getEmail()), e,
					HttpStatus.NOT_FOUND);
		}
		if (foundUsers.size() > 1) {
			throw new RestException(ErrorMessageEnum.MORE_THAN_ONE_USER_FOR_EMAIL.getMessage(loginRequest.getEmail()),
					new Exception(ErrorMessageEnum.MORE_THAN_ONE_USER_FOR_EMAIL.getMessage(loginRequest.getEmail())),
					HttpStatus.NOT_FOUND);
		}
		
		result=foundUsers.get(0);
		LOG.info("###getPassword###"+result.getPassword());
		LOG.info("###match::Password###"+passwordEncoder.encode(loginRequest.getPassword()));
		String encoded = new BCryptPasswordEncoder().encode(loginRequest.getPassword());
		LOG.info("##encoded###"+encoded);
		if (!passwordEncoder.matches(loginRequest.getPassword(), result.getPassword())) {
			throw new RestException(ErrorMessageEnum.WRONG_PASSWORD.getMessage(),
					new Exception(ErrorMessageEnum.WRONG_PASSWORD.getMessage()),
					HttpStatus.BAD_REQUEST);
		}
		//
		UsernamePasswordAuthenticationToken jwtAuth = new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());
		String token = jwtTokenProvider.generateToken(jwtAuth);
		result.setPassword(null);
		result.setToken(token);
		return ResponseEntity.ok(result);
	}
	
	@GetMapping(path="/status")
	public ResponseEntity<String> getStatus(){
		return ResponseEntity.ok("Status OK");
	}

}

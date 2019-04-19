package com.knoor.api.security;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.knoor.api.exception.BusinessException;
import com.knoor.api.model.db.UserModel;
import com.knoor.api.service.reactive.UserService;

@Service("userDetailsService")
public class KNUserDetailsService implements UserDetailsService {

	private static final Logger LOG = LoggerFactory.getLogger(KNUserDetailsService.class);

	@Autowired
	public KNUserDetailsService(UserService userService) {
		this.userService = userService;
	}

	private UserService userService;

	private Set<UserPrincipal> users = new HashSet<>();

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		try {
			UserModel model = userService.findByEmail(username);
			UserPrincipal userPrincipal = new UserPrincipal(model.getEmail(), model.getPassword(), model.getRoles());
			users.add(userPrincipal);
			return userPrincipal;
		} catch (BusinessException e) {
			LOG.error("User with email: " + username + " not found", e);
			throw new UsernameNotFoundException("Utilisater " + username + " non trouvé");
		}

	}

	public boolean verifyCredentials(UserPrincipal principal) {
		return users.stream().anyMatch(e -> e.getUsername().equals(principal.getUsername())
				&& e.getPassword().equals(principal.getPassword()));
	}
}

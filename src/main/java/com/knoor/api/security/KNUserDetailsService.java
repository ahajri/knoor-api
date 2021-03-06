package com.knoor.api.security;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.knoor.api.beans.QueryParam;
import com.knoor.api.enums.OperatorEnum;
import com.knoor.api.exception.BusinessException;
import com.knoor.api.model.HUser;
import com.knoor.api.service.CloudMongoService;

@Service("userDetailsService")
public class KNUserDetailsService implements UserDetailsService {

	private static final String USER_COLLECTION_NAME = "users";

	private static final Logger LOG = LoggerFactory.getLogger(KNUserDetailsService.class);

	@Autowired
	private CloudMongoService cloudMongoService;

	private Set<UserPrincipal> users = new HashSet<>();

	private Gson gson = new Gson();

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		QueryParam[] qp = new QueryParam[1];
		qp[0] = new QueryParam("email", OperatorEnum.EQ.name(), username);

		try {
			List<HUser> hUsers = cloudMongoService.search(USER_COLLECTION_NAME, qp).stream()
					.map(d -> gson.fromJson(gson.toJson(d), HUser.class)).collect(Collectors.toList());

			if (hUsers.size() > 1) {
				throw new UsernameNotFoundException("More than one user found for username: " + username);
			}
			HUser user = hUsers.get(0);
			UserPrincipal userPrincipal = new UserPrincipal(user.getEmail(), user.getPassword(),
					user.getRoles().stream().map(r -> r.getName()).collect(Collectors.toList()));
			users.add(userPrincipal);
			return userPrincipal;
		} catch (BusinessException e) {
			LOG.error("Utilisater " + username + " non trouvé", e);
			throw new UsernameNotFoundException("Utilisater " + username + " non trouvé");
		}

	}

	public boolean verifyCredentials(UserPrincipal principal) {
		return users.stream().anyMatch(e -> e.getUsername().equals(principal.getUsername())
				&& e.getPassword().equals(principal.getPassword()));
	}
}

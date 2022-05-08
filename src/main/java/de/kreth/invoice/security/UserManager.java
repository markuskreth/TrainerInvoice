package de.kreth.invoice.security;

import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.representations.AccessToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import de.kreth.invoice.data.User;
import de.kreth.invoice.persistence.UserRepository;

@Component
public class UserManager {

    private UserRepository userRepository;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
	this.userRepository = userRepository;
    }

    private AccessToken getAccessToken() {
	Authentication authentication = getAuthentication();
	KeycloakPrincipal<?> principal = (KeycloakPrincipal<?>) authentication.getPrincipal();
	KeycloakSecurityContext context = principal.getKeycloakSecurityContext();
	return context.getToken();
    }

    public User getLoggedInUser() {
	AccessToken accessToken = getAccessToken();
	if (accessToken != null) {
	    return userRepository.findByPrincipalId(accessToken.getSubject());
	}
	return null;
    }

    public User save(User entity) {
	return userRepository.save(entity);
    }

    private Authentication getAuthentication() {

	return SecurityContextHolder.getContext().getAuthentication();
    }

    public User create() {
	User user = new User();
	AccessToken accessToken = getAccessToken();
	user.setPrincipal(accessToken);
	return user;

    }
}

package de.kreth.invoice.security;

import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.representations.AccessToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import de.kreth.invoice.data.User;
import de.kreth.invoice.data.UserAdress;
import de.kreth.invoice.data.UserBank;
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
	    User user = userRepository.findByPrincipalId(accessToken.getSubject());

	    return user;
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
	AccessToken accessToken = getAccessToken();

	User user = new User();
	user.setPrincipal(accessToken);
	UserBank bank = new UserBank();
	bank.setUser(user);
	user.setBank(bank);
	UserAdress adress = new UserAdress();
	adress.setUser(user);
	user.setAdress(adress);

	return user;

    }
}

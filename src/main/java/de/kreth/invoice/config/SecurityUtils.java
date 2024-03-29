package de.kreth.invoice.config;

import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.vaadin.flow.shared.ApplicationConstants;

public class SecurityUtils {

    private SecurityUtils() {
    }

    /**
     * Tests if the request is an internal framework request. The test consists of
     * checking if the request parameter is present and if its value is consistent
     * with any of the request types know.
     *
     * @param request {@link HttpServletRequest}
     * @return true if is an internal framework request. False otherwise.
     */
    static boolean isFrameworkInternalRequest(HttpServletRequest request) {
	final String parameterValue = request.getParameter(ApplicationConstants.REQUEST_TYPE_PARAMETER);

	return parameterValue != null
		&& Stream.of(com.vaadin.flow.server.HandlerHelper.RequestType.values())
			.anyMatch(r -> r.getIdentifier().equals(parameterValue));
    }

    static boolean isUserLoggedIn() {
	Authentication authentication = SecurityContextHolder.getContext().getAuthentication(); //
	return authentication != null //
		&& !(authentication instanceof AnonymousAuthenticationToken) //
		&& authentication.isAuthenticated(); //
    }
}

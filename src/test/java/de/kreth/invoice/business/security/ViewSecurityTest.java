package de.kreth.invoice.business.security;

import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockOpaqueToken;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.keycloak.adapters.springsecurity.account.KeycloakRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
//@ActiveProfiles("test")
@Disabled
class ViewSecurityTest {

    @Autowired
    private WebTestClient client;

    @Test
    void givenUnauthenticated_whenCallService_thenIsUnauthorized() {
	this.client.get().uri("/")
		.exchange().expectStatus().isUnauthorized();
    }

    @Test
    void givenAuthenticatedMissingRole_whenCallServiceWithSecured_thenForbidden() {

	this.client.mutateWith(mockOpaqueToken()).get().uri("/")
		.exchange().expectStatus().isForbidden();
    }

    @Test
    void givenAuthenticated_whenCallServiceWithSecured_thenOk() {

	KeycloakRole role = new KeycloakRole("INVOICE_USER");
	this.client.mutateWith(mockOpaqueToken().authorities(role))
		.get().uri("/")
		.exchange().expectStatus().isOk();
    }
}

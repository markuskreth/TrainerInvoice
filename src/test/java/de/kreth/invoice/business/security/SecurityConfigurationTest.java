package de.kreth.invoice.business.security;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@WebMvcTest(controllers = DummyCatchAllController.class)
class SecurityConfigurationTest {

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@BeforeEach
	void setup() {
		this.mockMvc = MockMvcBuilders
				.webAppContextSetup(this.webApplicationContext)
				.apply(springSecurity())
				.build();
	}

	@Test
	@WithMockUser(username = "user", roles = { "USER" })
	void asLoggedInUser_ICantAccess() throws Exception {
		mockMvc.perform(get("/")).andExpect(status().isForbidden());
	}

	@Test
	@WithMockUser(username = "admin", roles = { "INVOICE_USER" })
	void asInvoiceUser_ICanAccess() throws Exception {
		mockMvc.perform(get("/")).andExpect(status().isOk());
	}
}

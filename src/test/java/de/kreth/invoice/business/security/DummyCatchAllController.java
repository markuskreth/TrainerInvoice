package de.kreth.invoice.business.security;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping
public class DummyCatchAllController {

	@GetMapping(path = "/**")
	@ResponseBody
	public String catchAll() {
		return "DummyCatchallController#catchAll";
	}
}

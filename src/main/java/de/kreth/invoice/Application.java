package de.kreth.invoice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;

import de.kreth.property2java.processor.Format;
import de.kreth.property2java.processor.GenerateProperty2Java;

/**
 * The entry point of the Spring Boot application.
 *
 * Use the @PWA annotation make the application installable on phones, tablets
 * and some desktop browsers.
 *
 */
@SpringBootApplication
@Theme(value = "trainerinvoice")
@PWA(name = "trainerinvoice", shortName = "trainerinvoice", offlineResources = {})
@NpmPackage(value = "line-awesome", version = "1.3.0")
@GenerateProperty2Java(resources = { "version.properties",
	"localization.properties" }, format = Format.WithInnerPropertyLoader)
public class Application extends SpringBootServletInitializer implements AppShellConfigurator {

    private static final long serialVersionUID = 8632833774084603989L;

    public static void main(String[] args) {
	SpringApplication.run(Application.class, args);
    }

}

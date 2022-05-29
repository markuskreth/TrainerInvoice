package de.kreth.invoice;

import java.awt.GraphicsEnvironment;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;

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
public class Application extends SpringBootServletInitializer implements AppShellConfigurator {

    private static final long serialVersionUID = 8632833774084603989L;
    private static ResourceBundle bundle = null;

    public static void main(String[] args) {
	String[] names = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();

	System.out.println("Found " + names.length + " fonts:");

	for (String name : names) {
	    System.out.println(name);
	}
	SpringApplication.run(Application.class, args);
    }

    public static String getString(Localization_Properties property) {
	if (bundle == null) {
	    bundle = resourceBundle();
	}

	return property.getString(bundle::getString);
    }

    @Bean
    static ResourceBundle resourceBundle() {

	ResourceBundle bundle = PropertyResourceBundle.getBundle("localization");
	return bundle;
    }
}

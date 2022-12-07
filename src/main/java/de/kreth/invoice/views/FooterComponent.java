package de.kreth.invoice.views;

import static de.kreth.invoice.Version_Properties.BUILD_DATETIME;
import static de.kreth.invoice.Version_Properties.PROJECT_VERSION;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;

import de.kreth.invoice.Version_Properties;

public class FooterComponent extends FormLayout {

    private static final long serialVersionUID = 4845822203421115202L;

    private static final Logger LOGGER = LoggerFactory
	    .getLogger(FooterComponent.class);

    private static final Properties VERSION = new Properties();
    static {
	String path = "/version.properties";
	try {
	    recursivelyLoadPropFromPath(FooterComponent.class, path, 0);
	} catch (Exception e) {
	    LOGGER.error("Error loading version properties file = " + path
		    + ", cause: " + e.getMessage());
	}
    }

    private static void recursivelyLoadPropFromPath(
	    Class<FooterComponent> thisClass, String path, int level)
	    throws IOException {

	URL resource = thisClass.getResource(path);
	if (resource != null) {
	    VERSION.load(resource.openStream());
	    LOGGER.info("Successfully loaded version info from " + resource);
	} else if (level < 4) {
	    recursivelyLoadPropFromPath(thisClass, "/.." + path, level + 1);
	} else {
	    throw new IOException("File not Found in any subdir of " + path);
	}

    }

    public FooterComponent() {

	Label copyright = new Label("\u00a9 Markus Kreth\u00A0");
	copyright.addClassName("formlayout-spacing");
	add(copyright);

	if (propertiesLoaded()) {

	    String dateTimeProperty = getString(BUILD_DATETIME);
	    SimpleDateFormat sourceFormat = new SimpleDateFormat(
		    "yyyy-MM-dd HH:mm:ss");
	    try {
		Date date = sourceFormat.parse(dateTimeProperty);
		dateTimeProperty = DateFormat.getDateTimeInstance(
			DateFormat.MEDIUM, DateFormat.SHORT).format(date);
	    } catch (ParseException e) {
		LOGGER.warn(
			"Unable to parse dateTimeProperty=" + dateTimeProperty,
			e);
	    }
	    Label vers = new Label(
		    "\u00A0Version: " + getString(PROJECT_VERSION));
	    Label buildTime = new Label("\u00A0Build: " + dateTimeProperty);
	    vers.addClassName("formlayout-spacing");
	    buildTime.addClassName("formlayout-spacing");
	    add(vers, buildTime);
	}

    }

    private boolean propertiesLoaded() {
	return !VERSION.isEmpty() && !getString(BUILD_DATETIME).contains("${");
    }

    private String getString(Version_Properties prop) {
	return prop.getText();
    }
}

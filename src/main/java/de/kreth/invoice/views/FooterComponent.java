package de.kreth.invoice.views;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class FooterComponent extends HorizontalLayout {

    private static final long serialVersionUID = 4845822203421115202L;

    private static final Logger LOGGER = LoggerFactory
	    .getLogger(FooterComponent.class);

    private static final Properties VERSION = new Properties();
    static {
	String path = "/../version.properties";
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

	Text copyright = new Text("&copy; Markus Kreth");
	add(copyright);

//	if (propertiesLoaded()) {
//
//	    String dateTimeProperty = Version_Properties.BUILD_DATETIME.getString(VERSION::getProperty);
//	    SimpleDateFormat sourceFormat = new SimpleDateFormat(
//		    "yyyy-MM-dd HH:mm:ss");
//	    sourceFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
//	    try {
//		Date date = sourceFormat.parse(dateTimeProperty);
//		dateTimeProperty = DateFormat.getDateTimeInstance(
//			DateFormat.MEDIUM, DateFormat.SHORT).format(date);
//	    } catch (ParseException e) {
//		LOGGER.warn(
//			"Unable to parse dateTimeProperty=" + dateTimeProperty,
//			e);
//	    }
//	    Label vers = new Label(
//		    "Version: " + Version_Properties.PROJECT_VERSION.getString(VERSION::getProperty));
//	    Label buildTime = new Label("Build: " + dateTimeProperty);
//	    adds(vers, buildTime);
//	}
    }

//    private boolean propertiesLoaded() {
//	return Version_Properties.BUILD_DATETIME.getString(VERSION::getProperty) != null
//		&& Version_Properties.BUILD_DATETIME.getString(VERSION::getProperty).trim().isEmpty() == false;
//    }
}

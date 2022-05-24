package de.kreth.invoice.views;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public final class ErrorNotification {

    public static void showError(String message) {
	Notification notification = new Notification();
	notification.addThemeVariants(NotificationVariant.LUMO_ERROR);

	Div text = new Div(new Text(message));

	Button closeButton = new Button(new Icon("lumo", "cross"));
	closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
	closeButton.getElement().setAttribute("aria-label", "Close");
	closeButton.addClickListener(event -> {
	    notification.close();
	});

	HorizontalLayout layout = new HorizontalLayout(text, closeButton);
	layout.setAlignItems(Alignment.CENTER);

	notification.add(layout);
	notification.open();
    }
}

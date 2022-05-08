package de.kreth.invoice.components;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class InvoiceOverviewComponent extends VerticalLayout {

    private static final long serialVersionUID = 1067257075519373200L;
    private final InvoiceGrid grid;

    public InvoiceOverviewComponent() {

	addClassName("bordered");

	this.grid = new InvoiceGrid();
	add(new H2("Rechnungen"), grid);
    }
}

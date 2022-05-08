package de.kreth.invoice.components;

import java.util.List;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import de.kreth.invoice.data.Article;
import de.kreth.invoice.data.InvoiceItem;
import de.kreth.invoice.data.User;
import de.kreth.invoice.persistence.ArticleRepository;
import de.kreth.invoice.persistence.InvoiceItemRepository;

public class InvoiceItemOverviewComponent extends VerticalLayout {

    private static final long serialVersionUID = -4486121981960039L;
    private final InvoiceItemGrid grid;
    private final InvoiceItemRepository invoiceItemRepository;
    private final ArticleRepository articleRepository;
    private final User user;

    public InvoiceItemOverviewComponent(InvoiceItemRepository invoiceItemRepository,
	    ArticleRepository articleRepository, User user) {
	this.invoiceItemRepository = invoiceItemRepository;
	this.articleRepository = articleRepository;
	this.user = user;
	Button addButton = new Button("Hinzufügen", this::createNewitem);
	add(new HorizontalLayout(new H3("Rechnungspositionen"), addButton));
	grid = new InvoiceItemGrid(invoiceItemRepository);
	add(grid);
    }

    public void refreshData() {
	grid.refreshData();
    }

    private void createNewitem(ClickEvent<Button> ev) {
	InvoiceItem item = new InvoiceItem();
	List<Article> articles = articleRepository.findByUserId(user.getId());
	InvoiceItemDialog dialog = new InvoiceItemDialog(articles, dlg -> {
	    if (dlg.isClosedWithOk()) {
		dlg.writeTo(item);
		invoiceItemRepository.save(item);
	    }
	});
	dialog.readFrom(item);
	dialog.setVisible();

    }
}

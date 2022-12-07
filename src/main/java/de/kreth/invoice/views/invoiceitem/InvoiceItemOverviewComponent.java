package de.kreth.invoice.views.invoiceitem;

import static de.kreth.invoice.Localization_Properties.CAPTION_INVOICEITEMS;
import static de.kreth.invoice.Localization_Properties.CAPTION_INVOICEITEM_ADD;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.grid.ItemClickEvent;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu.GridContextMenuItemClickEvent;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.selection.SelectionEvent;
import com.vaadin.flow.data.selection.SelectionListener;

import de.kreth.invoice.Localization_Properties;
import de.kreth.invoice.business.ArticleBusiness;
import de.kreth.invoice.business.InvoiceItemBusiness;
import de.kreth.invoice.data.Article;
import de.kreth.invoice.data.InvoiceItem;
import de.kreth.invoice.data.User;
import de.kreth.invoice.persistence.SportArtRepository;
import de.kreth.invoice.persistence.SportstaetteRepository;

public class InvoiceItemOverviewComponent extends VerticalLayout {

    private static final long serialVersionUID = -4486121981960039L;
    private final InvoiceItemGrid<InvoiceItem> grid;
    private final InvoiceItemBusiness invoiceItemRepository;
    private final ArticleBusiness articleRepository;
    private final User user;
    private final List<ItemSelectionChangeListener> selectListener;
    private final SportArtRepository sportArtRepository;
    private final SportstaetteRepository sportstaetteRepository;

    public InvoiceItemOverviewComponent(InvoiceItemBusiness invoiceItemRepository,
	    ArticleBusiness articleRepository, SportArtRepository sportArtRepository,
	    SportstaetteRepository sportstaetteRepository, User user) {
	this.invoiceItemRepository = invoiceItemRepository;
	this.articleRepository = articleRepository;
	this.sportArtRepository = sportArtRepository;
	this.sportstaetteRepository = sportstaetteRepository;
	this.user = user;
	this.selectListener = new ArrayList<>();

	Button addButton = new Button(CAPTION_INVOICEITEM_ADD.getText(), this::createNewitem);
	H3 title = new H3(CAPTION_INVOICEITEMS.getText());

	HorizontalLayout horizontalLayout = new HorizontalLayout(title, addButton);

	add(horizontalLayout);

	grid = new InvoiceItemGrid<>();
	grid.addItemClickListener(this::itemClicked);
	grid.setSelectionMode(SelectionMode.MULTI);
	GridContextMenu<InvoiceItem> contextMenu = grid.addContextMenu();
	contextMenu.addItem(Localization_Properties.LABEL_DELETE.getText(), this::deleteEvent);

	add(grid);
    }

    private void deleteEvent(GridContextMenuItemClickEvent<InvoiceItem> event) {
	if (event.getItem().isPresent()) {
	    ConfirmDialog dlg = new ConfirmDialog();
	    dlg.setHeader(Localization_Properties.MESSAGE_DELETE_TITLE.getText());
	    dlg.setText(MessageFormat.format(Localization_Properties.MESSAGE_DELETE_TEXT.getText(),
		    event.getItem().get()));
	    dlg.setCancelable(true);
	    dlg.setCancelText("Nicht " + Localization_Properties.LABEL_DELETE.getText());
	    dlg.setConfirmText(Localization_Properties.LABEL_DELETE.getText());
	    dlg.addConfirmListener(ev -> {
		invoiceItemRepository.delete(event.getItem().get());
		refreshData();
	    });
	    dlg.open();
	}
    }

    public void addSeelctionListener(ItemSelectionChangeListener listener) {
	selectListener.add(listener);

	grid.addSelectionListener(new SelectionListener<Grid<InvoiceItem>, InvoiceItem>() {

	    private static final long serialVersionUID = 597901593658493167L;

	    @Override
	    public void selectionChange(SelectionEvent<Grid<InvoiceItem>, InvoiceItem> event) {

		ItemSelectionChangeEvent ev;

		Set<InvoiceItem> selected = event.getAllSelectedItems();
		if (selected.isEmpty()) {
		    ev = new ItemSelectionChangeEvent(getAllItems());
		} else {
		    ev = new ItemSelectionChangeEvent(selected);
		}
		listener.itemSelectionChanged(ev);
	    }
	});
    }

    public void refreshData() {
	List<InvoiceItem> findByInvoiceIsNull = invoiceItemRepository.findByInvoiceIsNull(user);
	findByInvoiceIsNull.sort((i1, i2) -> i1.getStart().compareTo(i2.getStart()));
	grid.setItems(findByInvoiceIsNull);
	grid.deselectAll();
	grid.getDataProvider().refreshAll();
	ItemSelectionChangeEvent evt = new ItemSelectionChangeEvent(getAllItems());
	for (ItemSelectionChangeListener itemSelectionChangeListener : selectListener) {
	    itemSelectionChangeListener.itemSelectionChanged(evt);
	}
    }

    private void itemClicked(ItemClickEvent<InvoiceItem> event) {
	InvoiceItem item = event.getItem();
	if (event.getButton() == 0) {
	    editItem(item);
	}
    }

    private void createNewitem(ClickEvent<Button> ev) {
	InvoiceItem item = new InvoiceItem();
	editItem(item);
    }

    private void editItem(InvoiceItem item) {
	List<Article> articles = articleRepository.findByUserId(user.getId());
	if (!articles.isEmpty()) {
	    InvoiceItemDialog dialog = new InvoiceItemDialog(articles, sportArtRepository, sportstaetteRepository,
		    dlg -> {
			if (dlg.isClosedWithOk()) {
			    dlg.writeTo(item);
			    invoiceItemRepository.save(item);
			    refreshData();
			}
		    });
	    dialog.readFrom(item);
	    dialog.setVisible();
	} else {
	    ConfirmDialog dlg = new ConfirmDialog("Fehler", "Es sind keine Artikel gespeichert. Bitte zuerst anlegen.",
		    "Abbrechen", ev -> {
		    });
	    dlg.open();
	}
    }

    public List<InvoiceItem> getAllItems() {
	return new ArrayList<>(grid.getDataProvider().getItems());
    }

    public static interface ItemSelectionChangeListener {
	void itemSelectionChanged(ItemSelectionChangeEvent event);
    }

    public static class ItemSelectionChangeEvent {
	private List<InvoiceItem> values;

	public ItemSelectionChangeEvent(Collection<InvoiceItem> values) {
	    this.values = Collections.unmodifiableList(new ArrayList<>(values));
	}

	public List<InvoiceItem> getValues() {
	    return values;
	}
    }
}

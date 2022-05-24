package de.kreth.invoice.views.invoiceitem;

import static de.kreth.invoice.Application.getString;
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

public class InvoiceItemOverviewComponent extends VerticalLayout {

    private static final long serialVersionUID = -4486121981960039L;
    private final InvoiceItemGrid<InvoiceItem> grid;
    private final InvoiceItemBusiness invoiceItemRepository;
    private final ArticleBusiness articleRepository;
    private final User user;
    private final List<ItemSelectionChangeListener> selectListener;

    public InvoiceItemOverviewComponent(InvoiceItemBusiness invoiceItemRepository,
	    ArticleBusiness articleRepository, User user) {
	this.invoiceItemRepository = invoiceItemRepository;
	this.articleRepository = articleRepository;
	this.user = user;
	this.selectListener = new ArrayList<>();

	Button addButton = new Button(getString(CAPTION_INVOICEITEM_ADD), this::createNewitem);
	H3 title = new H3(getString(CAPTION_INVOICEITEMS));

	HorizontalLayout horizontalLayout = new HorizontalLayout(title, addButton);

	add(horizontalLayout);

	grid = new InvoiceItemGrid<>();
	grid.addItemClickListener(this::itemClicked);
	grid.setSelectionMode(SelectionMode.MULTI);
	GridContextMenu<InvoiceItem> contextMenu = grid.addContextMenu();
	contextMenu.addItem(getString(Localization_Properties.LABEL_DELETE), this::deleteEvent);

	add(grid);
    }

    private void deleteEvent(GridContextMenuItemClickEvent<InvoiceItem> event) {
	if (event.getItem().isPresent()) {
	    ConfirmDialog dlg = new ConfirmDialog();
	    dlg.setHeader(getString(Localization_Properties.MESSAGE_DELETE_TITLE));
	    dlg.setText(MessageFormat.format(getString(Localization_Properties.MESSAGE_DELETE_TEXT),
		    event.getItem().get()));
	    dlg.setCancelable(true);
	    dlg.setCancelText("Nicht " + getString(Localization_Properties.LABEL_DELETE));
	    dlg.setConfirmText(getString(Localization_Properties.LABEL_DELETE));
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
	grid.setItems(invoiceItemRepository.findByInvoiceIsNull(user));
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
	InvoiceItemDialog dialog = new InvoiceItemDialog(articles, dlg -> {
	    if (dlg.isClosedWithOk()) {
		dlg.writeTo(item);
		invoiceItemRepository.save(item);
		refreshData();
	    }
	});
	dialog.readFrom(item);
	dialog.setVisible();
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

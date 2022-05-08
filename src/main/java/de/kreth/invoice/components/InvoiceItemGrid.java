package de.kreth.invoice.components;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.vaadin.flow.component.grid.FooterRow;
import com.vaadin.flow.component.grid.FooterRow.FooterCell;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSelectionModel;
import com.vaadin.flow.data.provider.DataChangeEvent;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.DataProviderListener;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.data.renderer.NumberRenderer;

import de.kreth.invoice.data.InvoiceItem;
import de.kreth.invoice.persistence.InvoiceItemRepository;

class InvoiceItemGrid extends Grid<InvoiceItem> {

    private static final long serialVersionUID = -8653320112619816426L;

    private final DateTimeFormatter ofLocalizedDateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM);

    private FooterCell priceSumCell;

    private FooterCell countCell;

    private FooterCell dateSpan;

    private final List<InvoiceItem> items = new ArrayList<>();

    private InvoiceItemRepository repository;

    public InvoiceItemGrid(InvoiceItemRepository invoiceItemRepository) {

	this.repository = invoiceItemRepository;
	addClassName("bordered");
	Column<InvoiceItem> titleCol = addColumn(InvoiceItem::getTitle);
	titleCol.setId("Article");
	titleCol.setHeader("Artikel");

	Column<InvoiceItem> dateColumn = addColumn(new LocalDateTimeRenderer<>(InvoiceItem::getStart,
		DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)));
	dateColumn.setId("Date");
	dateColumn.setHeader("Datum");

	Column<InvoiceItem> startColumn = addColumn(new LocalDateTimeRenderer<>(InvoiceItem::getStart,
		DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)));
	startColumn.setId("Start");
	startColumn.setHeader("Beginn");

	Column<InvoiceItem> endColumn = addColumn(new LocalDateTimeRenderer<>(InvoiceItem::getEnd,
		DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)));
	endColumn.setId("Ende");
	endColumn.setHeader("Ende");

	Column<InvoiceItem> participantColumn = addColumn(InvoiceItem::getParticipants);
	participantColumn.setHeader("Teilnehmer");

	Column<InvoiceItem> sumPriceColumn = addColumn(
		new NumberRenderer<>(InvoiceItem::getSumPrice, NumberFormat.getCurrencyInstance()));
	sumPriceColumn.setId("price");
	sumPriceColumn.setHeader("Betrag");

//	setSortOrder(GridSortOrder.asc(dateColumn).thenAsc(startColumn));
	FooterRow footer = appendFooterRow();

	priceSumCell = footer.getCell(sumPriceColumn);
//	dateSpan = footer.join(dateColumn, startColumn, endColumn);
	dateSpan = footer.getCell(dateColumn);
	countCell = footer.getCell(titleCol);

//	addSelectionListener(this::selectionChanged);

	items.addAll(repository.findByInvoiceIsNull());

	ListDataProvider<InvoiceItem> dataProvider = new ListDataProvider<InvoiceItem>(items);
	setDataProvider(dataProvider);
	dataProvider.addDataProviderListener(new InnerDataProviderListener());
    }

    public void refreshData() {
	items.clear();
	items.addAll(repository.findByInvoiceIsNull());
    }

    @Override
    public GridSelectionModel<InvoiceItem> setSelectionMode(SelectionMode selectionMode) {
	GridSelectionModel<InvoiceItem> setSelectionMode = super.setSelectionMode(selectionMode);
//	setSelectionMode.addSelectionListener(this::selectionChanged);
	return setSelectionMode;
    }

//    @SuppressWarnings("unchecked")
//    private void selectionChanged(SelectionEvent<T> event) {
//	if (event.getAllSelectedItems().isEmpty()) {
//	    updateFooterWith(((ListDataProvider<T>) getDataProvider()).getItems());
//	} else {
//	    updateFooterWith(event.getAllSelectedItems());
//	}
//    }

    protected void internalSetDataProvider(DataProvider<InvoiceItem, ?> dataProvider) {

	if (!(dataProvider instanceof ListDataProvider)) {
	    throw new IllegalArgumentException("dataProvider must be an instance of ListDataProvider");
	}
//	super.internalSetDataProvider(dataProvider);
	dataProvider.addDataProviderListener(new InnerDataProviderListener());
	updateFooterWith(((ListDataProvider<InvoiceItem>) getDataProvider()).getItems());
    }

    private void updateFooterWith(Collection<InvoiceItem> selected) {
	BigDecimal priceSum = BigDecimal.ZERO;
	LocalDate min = null;
	LocalDate max = null;

	for (InvoiceItem t : selected) {
	    priceSum = priceSum.add(t.getSumPrice());
	    min = getMin(min, t.getStart().toLocalDate());
	    max = getMax(max, t.getEnd().toLocalDate());
	}

	priceSumCell.setText(NumberFormat.getCurrencyInstance().format(priceSum));
	if (min != null && max != null) {
	    dateSpan.setText(min.format(ofLocalizedDateFormatter) + " - " + max.format(ofLocalizedDateFormatter));
	} else {
	    dateSpan.setText("");
	}
	countCell.setText("Anzahl: " + selected.size());
    }

    private LocalDate getMax(LocalDate max, LocalDate localDate) {
	if (max == null) {
	    max = localDate;
	} else {
	    if (max.isBefore(localDate)) {
		max = localDate;
	    }
	}
	return max;
    }

    private LocalDate getMin(LocalDate min, LocalDate localDate) {
	if (min == null) {
	    min = localDate;
	} else {
	    if (min.isAfter(localDate)) {
		min = localDate;
	    }
	}
	return min;
    }

    private class InnerDataProviderListener implements DataProviderListener<InvoiceItem> {

	private static final long serialVersionUID = -6094992880488082586L;

	@Override
	public void onDataChange(DataChangeEvent<InvoiceItem> event) {
	    if (event.getSource() == getDataProvider()) {
		@SuppressWarnings("unchecked")
		ListDataProvider<InvoiceItem> provider = (ListDataProvider<InvoiceItem>) getDataProvider();
		updateFooterWith(provider.getItems());
	    }
	}

    }
}

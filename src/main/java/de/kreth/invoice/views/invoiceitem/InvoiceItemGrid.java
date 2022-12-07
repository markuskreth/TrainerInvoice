package de.kreth.invoice.views.invoiceitem;

import static de.kreth.invoice.Localization_Properties.CAPTION_INVOICEITEM_DATE;
import static de.kreth.invoice.Localization_Properties.CAPTION_INVOICEITEM_END;
import static de.kreth.invoice.Localization_Properties.CAPTION_INVOICEITEM_NAME;
import static de.kreth.invoice.Localization_Properties.CAPTION_INVOICEITEM_PARTICIPANTS;
import static de.kreth.invoice.Localization_Properties.CAPTION_INVOICEITEM_START;
import static de.kreth.invoice.Localization_Properties.CAPTION_INVOICEITEM_SUMPRICE;

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
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.data.provider.DataChangeEvent;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.DataProviderListener;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.data.renderer.NumberRenderer;
import com.vaadin.flow.data.selection.SelectionEvent;

import de.kreth.invoice.data.InvoiceItem;

public class InvoiceItemGrid<T extends InvoiceItem> extends Grid<T> {

    private static final long serialVersionUID = -8653320112619816426L;

    private final DateTimeFormatter ofLocalizedDateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM);

    private final List<T> items = new ArrayList<>();
    private FooterCell priceSumCell;

    private FooterCell countCell;

    private FooterCell dateSpan;

    public InvoiceItemGrid() {

	Column<T> articleColumn = addColumn(InvoiceItem::getTitle)
		.setHeader(CAPTION_INVOICEITEM_NAME.getText());

	LocalDateTimeRenderer<T> renderer = new LocalDateTimeRenderer<>(InvoiceItem::getStart,
		DateTimeFormatter.ofPattern("EEE, dd.MM.yyyy"));
	Column<T> dateColumn = addColumn(renderer).setHeader(CAPTION_INVOICEITEM_DATE.getText());
	dateColumn.setId("Date");

	Column<T> startColumn = addColumn(new LocalDateTimeRenderer<>(InvoiceItem::getStart,
		DateTimeFormatter.ofPattern("HH:mm")))
			.setHeader(CAPTION_INVOICEITEM_START.getText());

	Column<T> endColumn = addColumn(new LocalDateTimeRenderer<>(InvoiceItem::getEnd,
		DateTimeFormatter.ofPattern("HH:mm")))
			.setHeader(CAPTION_INVOICEITEM_END.getText());
	addColumn(InvoiceItem::getParticipants)
		.setHeader(CAPTION_INVOICEITEM_PARTICIPANTS.getText());

	Column<T> priceColumn = addColumn(
		new NumberRenderer<>(InvoiceItem::getSumPrice, NumberFormat.getCurrencyInstance()))
			.setHeader(CAPTION_INVOICEITEM_SUMPRICE.getText());

	FooterRow footer = appendFooterRow();
	footer = appendFooterRow();

	priceSumCell = footer.getCell(priceColumn);
	dateSpan = footer.join(dateColumn, startColumn, endColumn);
	countCell = footer.getCell(articleColumn);

	addSelectionListener(this::selectionChanged);

	setItems(DataProvider.ofCollection(items));
	this.sort(GridSortOrder.asc(dateColumn).thenAsc(startColumn).build());

	getDataProvider().addDataProviderListener(new InnerDataProviderListener());
    }

    @Override
    public ListDataProvider<T> getDataProvider() {

	@SuppressWarnings("unchecked")
	ListDataProvider<T> dataProvider = (ListDataProvider<T>) super.getDataProvider();
	return dataProvider;
    }

    public void setItems(List<T> collection) {
	items.clear();
	items.addAll(collection);
	getDataProvider().refreshAll();
    }

    @Override
    public GridSelectionModel<T> setSelectionMode(SelectionMode selectionMode) {
	GridSelectionModel<T> setSelectionMode = super.setSelectionMode(selectionMode);
	setSelectionMode.addSelectionListener(this::selectionChanged);
	return setSelectionMode;
    }

    private void selectionChanged(SelectionEvent<Grid<T>, T> event) {
	if (event.getAllSelectedItems().isEmpty()) {
	    updateFooterWith(getDataProvider().getItems());
	} else {
	    updateFooterWith(event.getAllSelectedItems());
	}
    }
//
//    @SuppressWarnings("unchecked")
//    @Override
//    protected void internalSetDataProvider(DataProvider<T, ?> dataProvider) {
//
//	if (!(dataProvider instanceof ListDataProvider)) {
//	    throw new IllegalArgumentException("dataProvider must be an instance of ListDataProvider");
//	}
//	super.internalSetDataProvider(dataProvider);
//	dataProvider.addDataProviderListener(new InnerDataProviderListener());
//	updateFooterWith(((ListDataProvider<T>) getDataProvider()).getItems());
//    }

    private void updateFooterWith(Collection<T> selected) {
	BigDecimal priceSum = BigDecimal.ZERO;
	LocalDate min = null;
	LocalDate max = null;

	for (T t : selected) {
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
	} else if (max.isBefore(localDate)) {
	    max = localDate;
	}
	return max;
    }

    private LocalDate getMin(LocalDate min, LocalDate localDate) {
	if (min == null) {
	    min = localDate;
	} else if (min.isAfter(localDate)) {
	    min = localDate;
	}
	return min;
    }

    private class InnerDataProviderListener implements DataProviderListener<T> {

	private static final long serialVersionUID = -6094992880488082586L;

	@Override
	public void onDataChange(DataChangeEvent<T> event) {
	    if (event.getSource() == getDataProvider()) {
		ListDataProvider<T> provider = getDataProvider();
		updateFooterWith(provider.getItems());
	    }
	}

    }
}

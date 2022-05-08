package de.kreth.invoice.components;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.dialog.GeneratedVaadinDialog.OpenedChangeEvent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.data.provider.Query;

import de.kreth.invoice.data.Article;
import de.kreth.invoice.data.InvoiceItem;

public class InvoiceItemDialog {

    private final Dialog dialog = new Dialog();
    private ComboBox<Article> article;
    private DatePicker startDate;
    private TimePicker startTime;
    private TimePicker endTime;
    private TextField participants;
    private boolean closedWithOk;
    private DialogCloseListener listener;

    public InvoiceItemDialog(InvoiceItem item, List<Article> articles, DialogCloseListener listener) {
	this(articles, listener);
	readFrom(item);
    }

    public InvoiceItemDialog(List<Article> articles, DialogCloseListener listener) {

	this.listener = listener;
	this.startDate = new DatePicker(LocalDate.now());
	startDate.setLabel("Datum");
	this.startTime = new TimePicker(LocalTime.of(17, 0));
	startTime.setLabel("Startzeit");
	this.endTime = new TimePicker(LocalTime.of(20, 0));
	this.participants = new TextField();
	this.participants.setLabel("Teilnehmer");
	article = new ComboBox<Article>("Artikel", articles);
	article.setItemLabelGenerator(Article::getTitle);

	Button ok = new Button("Speichern", this::closeWithOk);
	Button discard = new Button("Abbrechen", ev -> dialog.close());

	dialog.add(article, startDate, startTime, endTime, participants, new HorizontalLayout(ok, discard));

	dialog.addOpenedChangeListener(this::dialogCloseCalled);
    }

    private void dialogCloseCalled(OpenedChangeEvent<Dialog> ev) {
	if (!ev.isOpened()) {
	    listener.dialogClosed(this);
	}
    }

    public void setVisible() {
	closedWithOk = false;
	dialog.open();
    }

    private void closeWithOk(ClickEvent<Button> ev) {
	closedWithOk = true;
	dialog.close();
    }

    public boolean isClosedWithOk() {
	return closedWithOk;
    }

    public void readFrom(InvoiceItem item) {

	if (item.getArticle() != null) {
	    article.setValue(item.getArticle());
	} else if (article.getDataProvider().size(new Query<>()) == 1) {
	    article.getDataProvider().fetch(new Query<>())
		    .findAny().ifPresent(article::setValue);
	}
	if (item.getStart() != null) {
	    startDate.setValue(item.getStart().toLocalDate());
	    startTime.setValue(item.getStart().toLocalTime());
	}
	if (item.getEnd() != null) {
	    endTime.setValue(item.getEnd().toLocalTime());
	} else if (item.getStart() != null) {
	    endTime.setValue(item.getStart().toLocalTime().plusHours(1));
	}
	if (item.getParticipants() != null) {
	    this.participants.setValue(item.getParticipants());
	}
    }

    public void writeTo(InvoiceItem item) {
	if (!closedWithOk) {
	    throw new IllegalStateException("Dialog was not closed by OK Button, writing is not possible.");
	}
	item.setArticle(article.getValue());
	item.setChangeDate(LocalDateTime.now());
	if (item.getCreatedDate() == null) {
	    item.setCreatedDate(item.getChangeDate());
	}
	item.setStart(LocalDateTime.of(startDate.getValue(), startTime.getValue()));
	item.setEnd(LocalDateTime.of(startDate.getValue(), endTime.getValue()));
	item.setParticipants(participants.getValue());

    }

    public interface DialogCloseListener {
	void dialogClosed(InvoiceItemDialog source);
    }
}

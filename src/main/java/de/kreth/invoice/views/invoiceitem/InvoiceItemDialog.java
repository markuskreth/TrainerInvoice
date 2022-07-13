package de.kreth.invoice.views.invoiceitem;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.GeneratedVaadinComboBox.CustomValueSetEvent;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.dialog.GeneratedVaadinDialog.OpenedChangeEvent;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.data.provider.Query;

import de.kreth.invoice.data.Article;
import de.kreth.invoice.data.InvoiceItem;
import de.kreth.invoice.data.SportArt;
import de.kreth.invoice.data.SportStaette;
import de.kreth.invoice.persistence.SportArtRepository;
import de.kreth.invoice.persistence.SportstaetteRepository;

public class InvoiceItemDialog {

    private final Dialog dialog = new Dialog();
    private ComboBox<Article> article;
    private DatePicker startDate;
    private TimePicker startTime;
    private TimePicker endTime;
    private TextField participants;
    private ComboBox<SportArt> sportart;
    private ComboBox<SportStaette> sportstaette;
    private boolean closedWithOk;
    private DialogCloseListener listener;
    private SportArtRepository sportarten;
    private SportstaetteRepository sportstaetten;
    private long userId;

    public InvoiceItemDialog(InvoiceItem item, List<Article> articles, SportArtRepository sportarten,
	    SportstaetteRepository sportstaetten, DialogCloseListener listener) {
	this(articles, sportarten, sportstaetten, listener);
	readFrom(item);
    }

    public InvoiceItemDialog(List<Article> articles, SportArtRepository sportarten,
	    SportstaetteRepository sportstaetten,
	    DialogCloseListener listener) {

	this.listener = listener;
	this.sportarten = sportarten;
	this.sportstaetten = sportstaetten;
	this.startDate = new DatePicker(LocalDate.now());
	startDate.setLabel("Datum");
	this.startTime = new TimePicker(LocalTime.of(17, 0));
	startTime.setLabel("Startzeit");
	this.endTime = new TimePicker(LocalTime.of(20, 0));
	this.participants = new TextField();
	this.participants.setLabel("Teilnehmer");
	article = new ComboBox<Article>("Artikel", articles);
	article.setItemLabelGenerator(Article::getTitle);

	this.userId = articles.get(0).getUserId();

	sportart = new ComboBox<SportArt>("Sportart");
	sportart.setItemLabelGenerator(SportArt::getName);
	sportart.setAllowCustomValue(true);
	sportart.addCustomValueSetListener(this::newCustomSportart);

	sportstaette = new ComboBox<SportStaette>("Sportstätte");
	sportstaette.setItemLabelGenerator(SportStaette::getName);
	sportstaette.setAllowCustomValue(true);

	sportstaette.addCustomValueSetListener(this::newCustomSportstaette);

	Button ok = new Button("Speichern", this::closeWithOk);
	Button discard = new Button("Abbrechen", ev -> dialog.close());

	dialog.add(article, sportart, sportstaette, startDate, startTime, endTime, participants,
		new HorizontalLayout(ok, discard));

	dialog.addOpenedChangeListener(this::dialogCloseCalled);
    }

    private void newCustomSportstaette(CustomValueSetEvent<ComboBox<SportStaette>> event) {

	Label text = new Label("Die Sportstätte wurde noch nie verwendet. Soll sie neu angelegt werden?");
	Label t2 = new Label("Sportstätte: " + event.getDetail());

	Button cancel = new Button("Abbrechen");
	Button store = new Button("Speichern");
	FormLayout buttonLayout = new FormLayout(store, cancel);
	FormLayout dlgLayout = new FormLayout(text, t2, buttonLayout);
	Dialog dlg = new Dialog();
	dlg.add(dlgLayout);
	cancel.addClickListener(e1 -> {
	    List<SportStaette> items = refreshSportstaetten();
	    if (!items.isEmpty()) {
		sportstaette.setValue(items.get(0));
	    } else {
		this.sportstaette.clear();
	    }
	    dlg.close();
	});
	store.addClickListener(e -> {
	    SportStaette sportStaette = new SportStaette();
	    sportStaette.setName(event.getDetail());
	    sportStaette.setUserId(article.getValue().getUserId());
	    sportStaette = sportstaetten.save(sportStaette);
	    refreshSportstaetten();
	    this.sportstaette.setValue(sportStaette);
	    dlg.close();
	});
	dlg.open();
    }

    private void newCustomSportart(CustomValueSetEvent<ComboBox<SportArt>> event) {
	Label text = new Label("Die Sportart wurde noch nie verwendet. Soll sie neu angelegt werden?");
	Label t2 = new Label("Sportart: " + event.getDetail());

	Button cancel = new Button("Abbrechen");
	Button store = new Button("Speichern");
	FormLayout buttonLayout = new FormLayout(store, cancel);
	FormLayout dlgLayout = new FormLayout(text, t2, buttonLayout);
	Dialog dlg = new Dialog();
	dlg.add(dlgLayout);
	cancel.addClickListener(e1 -> {
	    List<SportArt> sportartItems = refreshSportarten();
	    if (!sportartItems.isEmpty()) {
		sportart.setValue(sportartItems.get(0));
	    }
	    dlg.close();
	});
	store.addClickListener(e -> {
	    SportArt sportArt = new SportArt();
	    sportArt.setName(event.getDetail());
	    sportArt.setUserId(article.getValue().getUserId());
	    sportArt = sportarten.save(sportArt);
	    refreshSportarten();
	    this.sportart.setValue(sportArt);
	    dlg.close();
	});
	dlg.open();
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

	refreshSportarten();
	refreshSportstaetten();
	if (item.getSportStaette() != null) {
	    sportstaette.setValue(item.getSportStaette());
	}
	if (item.getSportArt() != null) {
	    sportart.setValue(item.getSportArt());
	}
    }

    private List<SportStaette> refreshSportstaetten() {
	List<SportStaette> sportstaettenItems = sportstaetten.findByUserId(userId);
	sportstaette.setItems(sportstaettenItems);
	if (!sportstaettenItems.isEmpty()) {
	    sportstaette.setAutoOpen(false);
	} else {
	    sportstaette.setAutoOpen(true);
	}
	return sportstaettenItems;
    }

    private List<SportArt> refreshSportarten() {
	List<SportArt> sportartItems = sportarten.findByUserId(userId);
	sportart.setItems(sportartItems);

	if (!sportartItems.isEmpty()) {
	    sportart.setAutoOpen(true);
	} else {
	    sportart.setAutoOpen(false);
	}

	return sportartItems;
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
	item.setSportArt(sportart.getValue());
	item.setSportStaette(sportstaette.getValue());
	item.getSumPrice();
    }

    public interface DialogCloseListener {
	void dialogClosed(InvoiceItemDialog source);
    }
}

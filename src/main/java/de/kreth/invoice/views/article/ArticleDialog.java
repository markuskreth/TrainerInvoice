package de.kreth.invoice.views.article;

import static de.kreth.invoice.Application.getString;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

import de.kreth.invoice.Localization_Properties;
import de.kreth.invoice.business.ArticleBusiness;
import de.kreth.invoice.data.Article;
import de.kreth.invoice.data.ReportLicense;
import de.kreth.invoice.data.User;
import de.kreth.invoice.views.PriceConverter;

public class ArticleDialog extends Dialog {

    private static final long serialVersionUID = 2516636187686436452L;

    private final ArticleBusiness business;

    private TextField title;

    private TextField pricePerHour;

    private TextField description;

    private Grid<Article> articleGrid;

    private User user;

    private Article current = null;

    private Binder<Article> binder;

    private Button discartButton;

    private Button storeButton;

    private Checkbox isTrainer;

    private Button deleteButton;

    public ArticleDialog(ArticleBusiness articleBusiness, User user) {
	this.business = articleBusiness;
	title = new TextField();
	title.setLabel("Artikel");
	pricePerHour = new TextField();
	pricePerHour.setLabel("Preis Einzel");

	description = new TextField();
	description.setLabel("Beschreibung");

	isTrainer = new Checkbox("als Trainer", false);

	FormLayout contentValues = new FormLayout();
	contentValues.add(title, pricePerHour, description, isTrainer);

	Button addArticle = new Button("Artikel hinzufügen");

	addArticle.addClickListener(ev -> {
	    current = createNewArticle();
	    binder.setBean(current);
	    toggleEditFields();
	});

	discartButton = new Button("Abbrechen", e -> {
	    binder.readBean(null);
	    current = null;
	    toggleEditFields();
	});
	discartButton.setVisible(false);

	storeButton = new Button("Speichern", e -> {
	    if (binder.validate().isOk()) {
		business.save(current);
		reloadItems();
		toggleEditFields();
	    }
	});

	Button closeButton = new Button(getString(Localization_Properties.LABEL_CLOSE),
		ev -> close());

	deleteButton = new Button(getString(Localization_Properties.LABEL_DELETE), ev -> {
	    business.delete(current);
	    current = null;
	    binder.setBean(null);
	    reloadItems();
	    toggleEditFields();
	});

	FormLayout contentButtons = new FormLayout();
	contentButtons.add(storeButton, discartButton, addArticle, deleteButton, closeButton);

	setupArticleGrid();

	setupBinder();

	FormLayout content = new FormLayout();
	content.add(contentValues, articleGrid, contentButtons);
	add(content);
	setWidth(80.0f, Unit.PERCENTAGE);
	setModal(true);
	setUser(user);
    }

    private void setUser(User user) {
	this.user = user;
	if (user != null) {
	    List<Article> loadAll = reloadItems();

	    if (loadAll.isEmpty()) {
		current = createNewArticle();
	    } else {
		current = loadAll.get(0);
		articleGrid.select(current);
	    }

	    binder.setBean(current);
	    toggleEditFields();
	} else {
	    close();
	}
    }

    private void toggleEditFields() {
	if (current == null || binder.getBean() == null) {
	    title.setEnabled(false);
	    pricePerHour.setEnabled(false);
	    description.setEnabled(false);
	    isTrainer.setEnabled(false);
	    discartButton.setVisible(false);
	    storeButton.setVisible(false);
	    deleteButton.setVisible(false);
	} else {
	    discartButton.setVisible(true);
	    storeButton.setVisible(true);
	    deleteButton.setVisible(true);
	    storeButton.setEnabled(binder.validate().isOk());
	    title.setEnabled(true);
	    pricePerHour.setEnabled(true);
	    description.setEnabled(true);
	    isTrainer.setEnabled(true);
	}
    }

    private Article createNewArticle() {
	Article article = new Article();
	article.setTitle("");
	article.setDescription("");
	article.setPricePerHour(BigDecimal.ZERO);
	article.setReport(ReportLicense.ASSISTANT.getRessource());
	if (user != null) {
	    article.setUserId(user.getId());
	} else {
	    article.setUserId(-1);
	}
	return article;
    }

    private List<Article> reloadItems() {
	List<Article> loadAll = business.loadAll(a -> a.getUserId() == user.getId());
	articleGrid.setItems(loadAll);
	return loadAll;
    }

    private void setupBinder() {
	binder = new Binder<>(Article.class);
	binder.forField(title).asRequired().withNullRepresentation("").bind(Article::getTitle, Article::setTitle);
	PriceConverter converter = new PriceConverter(BigDecimal.ZERO, "Ungültiger Preis");

	binder.forField(pricePerHour).asRequired()
		.withNullRepresentation("")
		.withConverter(converter)
		.withValidator(t -> t != null && t.doubleValue() > 0, "Der Preis muss größer 0 sein!")
		.bind(Article::getPricePerHour, Article::setPricePerHour);

	binder.forField(description).withNullRepresentation("").bind(Article::getDescription, Article::setDescription);

	binder.forField(isTrainer).bind(this::reportToCheckbox, this::checkboxToReportLicense);

	binder.addValueChangeListener(changeEv -> {
	    toggleEditFields();
	});
    }

    private boolean reportToCheckbox(Article source) {
	if (source == null || source.getReport() == null) {
	    return false;
	}
	return source.getReport().contentEquals(ReportLicense.TRAINER.getRessource());
    }

    private void checkboxToReportLicense(Article bean, Boolean fieldvalue) {
	if (bean != null && fieldvalue != null) {
	    if (fieldvalue) {
		bean.setReport(ReportLicense.TRAINER.getRessource());
	    } else {
		bean.setReport(ReportLicense.ASSISTANT.getRessource());
	    }
	}
    }

    private void setupArticleGrid() {
	articleGrid = new Grid<>();

	articleGrid.addColumn(Article::getTitle).setHeader("Titel");

//	ValueProvider<BigDecimal, String> currencyProvider = new ValueProvider<BigDecimal, String>() {
//	    private static final long serialVersionUID = -6305095230785149948L;
//	    private final NumberFormat formatter = NumberFormat.getCurrencyInstance();
//
//	    @Override
//	    public String apply(BigDecimal source) {
//		return formatter.format(source.doubleValue());
//	    }
//	};
//	articleGrid.addColumn(Article::getPricePerHour, currencyProvider);
	articleGrid.addColumn(Article::getDescription)
		.setHeader("Beschreibung");
	articleGrid.addColumn(this::reportToCheckbox)
		.setHeader("Report");

	articleGrid.addSelectionListener(sel -> {
	    if (!binder.hasChanges()) {

		Optional<Article> selected = sel.getFirstSelectedItem();
		if (selected.isPresent()) {
		    current = selected.get();
		    binder.setBean(current);

		    boolean hasItems = business.hasInvoiceItem(current);
		    deleteButton.setEnabled(!hasItems);

		    if (hasItems) {
//			deleteButton.setT
		    } else {
//			deleteButton.setDescription(
//				"Es existieren Einträge zu diesem Artikel. Er kann nicht gelöscht werden.");
		    }

//		    if (hasInvoice) {
//			String errorMessage = getString(MESSAGE_ARTICLE_ERROR_INVOICEEXISTS);
//			title.setDescription(errorMessage);
//			pricePerHour.setDescription(errorMessage);
//			description.setDescription(errorMessage);
//
//		    } else {
//			title.setDescription("");
//			pricePerHour.setDescription("");
//			description.setDescription("");
//		    }
		}
	    }
	    toggleEditFields();
	});
    }

}

package de.kreth.invoice.views;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.server.AbstractStreamResource;
import com.vaadin.flow.server.InputStreamFactory;
import com.vaadin.flow.server.StreamResource;

import de.kreth.invoice.data.Adress;
import de.kreth.invoice.data.User;
import de.kreth.invoice.data.UserAdress;
import de.kreth.invoice.data.UserBank;
import de.kreth.invoice.persistence.UserAdressRepository;
import de.kreth.invoice.persistence.UserBankRepository;
import de.kreth.invoice.report.Signature;

public class UserDetailsDialog extends Dialog {

    private static final long serialVersionUID = -6255487997073609068L;

    private Logger logger = LoggerFactory.getLogger(getClass());

    private final Binder<UserBank> bankBinder = new Binder<>();
    private final Binder<UserAdress> adressBinder = new Binder<>();

    private final TextField loginName;

    private final TextField prename;

    private final TextField surname;

    private final TextField bankName;

    private final TextField iban;

    private final TextField bic;

    private final TextField adress1;

    private final TextField adress2;

    private final TextField zipCode;

    private final TextField city;

    private final Button okButton;

    private final Image signatureImage;

    private final UserBankRepository bankRepository;
    private final UserAdressRepository adressRepository;

    private User user;

    private UserBank bank;

    private UserAdress adress;

    public UserDetailsDialog(UserBankRepository bankRepository, UserAdressRepository adressRepository) {
	this.bankRepository = bankRepository;
	this.adressRepository = adressRepository;

	loginName = new TextField();
	loginName.setLabel("Login Name");
	loginName.setEnabled(false);

	prename = new TextField();
	prename.setLabel("Vorname");
	prename.setEnabled(false);

	surname = new TextField();
	surname.setLabel("Nachname");

	bankName = new TextField();
	bankName.setLabel("Name der Bank");
	bankBinder.forField(bankName)
		.asRequired("Der BankName darf nicht leer sein.")
		.bind(UserBank::getBankName, UserBank::setBankName);

	iban = new TextField();
	iban.setLabel("IBAN");
	bankBinder.forField(iban)
		.asRequired("Die IBAN darf nicht leer sein.")
		.bind(UserBank::getIban, UserBank::setIban);

	bic = new TextField();
	bic.setLabel("BIC");
	bankBinder.forField(bic).bind(UserBank::getBic, UserBank::setBic);

	adress1 = new TextField();
	adress1.setLabel("Straße");
	adressBinder.forField(adress1)
		.asRequired("Die Straße muss angegeben sein.")
		.bind(Adress::getAdress1, Adress::setAdress1);

	adress2 = new TextField();
	adress2.setLabel("Adresszusatz");
	adressBinder.forField(adress2)
		.bind(Adress::getAdress2, Adress::setAdress2);

	zipCode = new TextField();
	zipCode.setLabel("Postleitzahl");
	adressBinder.forField(zipCode)
		.asRequired("Die Postleitzahl muss angegeben sein.")
		.bind(Adress::getZip, Adress::setZip);

	city = new TextField();
	city.setLabel("Ort");
	adressBinder.forField(city)
		.asRequired("Der Ort muss angegeben sein.")
		.bind(Adress::getCity, Adress::setCity);

	signatureImage = new Image();
	signatureImage.setWidth("192px");
	signatureImage.setHeight("62px");
	signatureImage.setAlt("Keine Unterschrift konfiguriert");

	Upload upload = new Upload(this::receiveUpload);
	upload.addFinishedListener(ev -> updateSignatureImage());

	VerticalLayout layout = new VerticalLayout();
	layout.add(loginName, prename, surname);
	layout.add(new Hr());
	layout.add(bankName, iban, bic);
	layout.add(new Hr());
	HorizontalLayout cityLayout = new HorizontalLayout();
	cityLayout.add(zipCode, city);

	layout.add(adress1, adress2, cityLayout, new HorizontalLayout(signatureImage, upload));

	okButton = new Button("OK", ev -> {
	    BinderValidationStatus<UserBank> bankValidation = bankBinder.validate();

	    BinderValidationStatus<UserAdress> adressValidation = adressBinder.validate();
	    if (bankValidation.isOk() && adressValidation.isOk()) {
		close();
	    }
	});

	Button cancel = new Button("Abbrechen", ev -> close());

	HorizontalLayout buttons = new HorizontalLayout();
	buttons.add(okButton, cancel);
	layout.add(buttons);

	add(layout);
    }

    private OutputStream receiveUpload(String filename, String mimeType) {

	Signature signature = new Signature(user);
	try {
	    return signature.createOutputStream(filename);
	} catch (IOException e) {
	    throw new UncheckedIOException(e);
	}
    }
//
//    public Registration addOkClickListener(ClickListener listener) {
//	return okButton.addClickListener(listener);
//    }

    public void setUser(User user) {
	this.user = Objects.requireNonNull(user);
	this.bank = bankRepository.findByUser(user);
	this.adress = adressRepository.findByUser(user);
	if (this.bank == null) {
	    this.bank = new UserBank();
	}
	if (this.adress == null) {
	    this.adress = new UserAdress();
	}
	updateSignatureImage();
    }

    private void updateSignatureImage() {
	if (user != null) {
	    Signature signature = new Signature(user);
	    if (signature.isSignatureImageExists()) {
		File signatureUrl = signature.getSignatureUrl();
		logger.info("Showing signature: {}", signatureUrl);

		StreamResource resource = new StreamResource(getAriaLabelString(), new InputStreamFactory() {

		    private static final long serialVersionUID = 1L;

		    @Override
		    public InputStream createInputStream() {
			try {
			    return new FileInputStream(signatureUrl);
			} catch (FileNotFoundException e) {
			    throw new UncheckedIOException(e);
			}
		    }
		});
		signatureImage.setSrc(resource);
	    } else {
		signatureImage.setSrc((AbstractStreamResource) null);
	    }
	}
    }

    public User getUser() {
	return user;
    }

}

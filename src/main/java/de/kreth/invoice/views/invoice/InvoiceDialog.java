package de.kreth.invoice.views.invoice;

import static de.kreth.invoice.Application.getString;
import static de.kreth.invoice.Localization_Properties.CAPTION_INVOICE_PRINTSIGNATURE;
import static de.kreth.invoice.Localization_Properties.LABEL_CANCEL;
import static de.kreth.invoice.Localization_Properties.LABEL_CLOSE;
import static de.kreth.invoice.Localization_Properties.LABEL_OPEN;
import static de.kreth.invoice.Localization_Properties.LABEL_PREVIEW;
import static de.kreth.invoice.Localization_Properties.LABEL_STORE;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HasValue.ValueChangeEvent;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.StreamResourceWriter;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.shared.Registration;

import de.kreth.invoice.data.Invoice;
import de.kreth.invoice.data.InvoiceItem;
import de.kreth.invoice.report.InvoiceReportSource;
import de.kreth.invoice.report.Signature;
import de.kreth.invoice.views.invoiceitem.InvoiceItemGrid;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

public class InvoiceDialog extends Dialog {

    private static final long serialVersionUID = -8997281625128779760L;

    private static final Logger LOGGER = LoggerFactory.getLogger(InvoiceDialog.class);

    public enum InvoiceMode {
	CREATE,
	VIEW_ONLY
    }

    private TextField invoiceNo;

    private DatePicker invoiceDate;

    private InvoiceItemGrid<InvoiceItem> itemGrid;

    private Button okButton;

    private Invoice invoice;

    private Signature signature;

    private Checkbox printSignature;

    private final List<String> existingInvoiceNumbers = new ArrayList<>();

    private Button deleteButton;

    /**
     * Initializes the Dialog with an empty {@link Invoice}.
     * <p>
     * Be sure to set an {@link Invoice} with at least 1 Item with
     * {@link #setInvoice(Invoice)}.
     * <p>
     *
     * @param resBundle
     * @param pdfOpenLabel
     */
    public InvoiceDialog(InvoiceMode pdfOpenLabel) {
	setWidth(200, Unit.EM);

	invoiceNo = new TextField();
	invoiceNo.setLabel("Rechn.Nr.");
	if (InvoiceMode.VIEW_ONLY == pdfOpenLabel) {
	    invoiceNo.setReadOnly(true);
	} else {
	    invoiceNo.addValueChangeListener(this::updateInvoiceNo);
	}

	invoiceDate = new DatePicker();
	invoiceDate.setLabel("Rechnung Datum");
	invoiceDate.setEnabled(false);

	itemGrid = new InvoiceItemGrid<>();

	printSignature = new Checkbox(getString(CAPTION_INVOICE_PRINTSIGNATURE));
	if (InvoiceMode.VIEW_ONLY == pdfOpenLabel) {
	    printSignature.setEnabled(false);
	}

	printSignature.addValueChangeListener(ev -> {
	    if (printSignature.getValue() == Boolean.TRUE) {
		invoice.setSignImagePath(signature.getSignatureUrl().getAbsolutePath());
	    } else {
		invoice.setSignImagePath(null);
	    }
	});
	okButton = new Button(getString(LABEL_STORE), ev -> close());
	deleteButton = new Button("Löschen");

	String previewCaption;
	String closeCaption;
	if (pdfOpenLabel == InvoiceMode.VIEW_ONLY) {
	    previewCaption = getString(LABEL_OPEN);
	    closeCaption = getString(LABEL_CLOSE);
	    okButton.setVisible(false);
	} else {
	    previewCaption = getString(LABEL_PREVIEW);
	    closeCaption = getString(LABEL_CANCEL);
	    deleteButton.setVisible(false);
	}
	Button cancel = new Button(closeCaption, ev -> close());
	Button previewButton = new Button(previewCaption, this::showPdf);

	HorizontalLayout btnLayout = new HorizontalLayout();
	btnLayout.add(okButton, cancel, previewButton, deleteButton);

	VerticalLayout vLayout = new VerticalLayout();

	vLayout.add(invoiceNo, invoiceDate, itemGrid, printSignature);

	vLayout.add(btnLayout);
	vLayout.setHorizontalComponentAlignment(Alignment.BASELINE, btnLayout);

	add(vLayout);
    }

    private void updateInvoiceNo(ValueChangeEvent<String> ev) {
	if (invoice != null) {
	    invoice.setInvoiceId(ev.getValue());
	    if (existingInvoiceNumbers.contains(invoice.getInvoiceId())) {
		invoiceNo.setErrorMessage("Die Rechnungsnummer existiert bereits. Sie muss eindeutig sein.");
		invoiceNo.setInvalid(true);
		okButton.setEnabled(false);
	    } else {
		invoiceNo.setInvalid(false);
		okButton.setEnabled(true);
	    }
	}
    }

    private void showPdf(ClickEvent<Button> ev) {
	try {
	    JasperPrint print = createJasperPrint();
	    LOGGER.debug("Created JasperPrint");
	    showInWebWindow(print, ev);
	} catch (JRException | IOException e) {
	    throw new RuntimeException(e);
	}
    }

    private void showInWebWindow(JasperPrint print, ClickEvent<Button> ev) throws IOException, JRException {

	Dialog window = new Dialog();
//	window.setCaption("View PDF");
	Component e = createEmbedded(print);
	window.add(e);
	window.setModal(true);
//	window.setSizeFull();
	window.setWidth("95%");
	window.setHeight("99%");
	window.open();
    }

    private Component createEmbedded(JasperPrint print) throws IOException, JRException {

	PipedInputStream inFrame = new PipedInputStream();

	final StreamResource resourceFrame = new StreamResource("invoice.pdf", () -> inFrame);
	resourceFrame.setHeader("Content-Type", "application/pdf");

	Element image = new Element("object");
	image.setAttribute("type", "application/pdf");
	image.getStyle().set("display", "block");
	image.setAttribute("data", resourceFrame);
	image.setAttribute("width", "100%");
	image.setAttribute("height", "100%");

	ExecutorService exec = Executors.newSingleThreadExecutor();
	File outFile = Files.createTempFile("invoice", ".pdf").toFile();
	exec.execute(() -> {
	    try (PipedOutputStream out1 = new PipedOutputStream(inFrame)) {
		JasperExportManager.exportReportToPdfStream(print, out1);
	    } catch (JRException | IOException e) {
		LOGGER.error("Error exporting Report to Browser Window", e);
	    }
	});
	exec.shutdown();

	JasperExportManager.exportReportToPdfFile(print, outFile.getAbsolutePath());
	LOGGER.info("PDF File written: {}", outFile.getAbsolutePath());

	StreamResourceWriter inStream = new StreamResourceWriter() {

	    private static final long serialVersionUID = -3847642428860682957L;

	    @Override
	    public void accept(OutputStream stream, VaadinSession session) throws IOException {
		try (FileInputStream in = new FileInputStream(outFile)) {
		    in.transferTo(stream);
		}
	    }
	};

	new StreamResource("invoice.pdf", inStream);
	Anchor link = new Anchor(resourceFrame, "Download PDF");

	link.addFocusListener(ev -> LOGGER.debug("Download link clicked."));
	link.addAttachListener(ev -> LOGGER.debug("Download link attached."));
	VerticalLayout layout = new VerticalLayout();
	layout.add(link);
	layout.getElement().appendChild(image);
	layout.setSizeFull();
	return layout;
    }

    private JasperPrint createJasperPrint() throws JRException {
	InvoiceReportSource source = InvoiceReportSource.create(invoice);
	JasperReport report = JasperCompileManager
		.compileReport(getClass().getResourceAsStream(invoice.getReportRessource()));
	return JasperFillManager.fillReport(report, new HashMap<>(), source);
    }

    public Registration addOkClickListener(ComponentEventListener<ClickEvent<Button>> listener) {
	return okButton.addClickListener(listener);
    }

    public Registration addDeleteClickListener(ComponentEventListener<ClickEvent<Button>> listener) {
	return deleteButton.addClickListener(listener);
    }

    public void setInvoice(Invoice invoice, List<String> existingInvoiceNumbers) {
	this.invoice = Objects.requireNonNull(invoice);
	this.existingInvoiceNumbers.clear();
	this.existingInvoiceNumbers.addAll(existingInvoiceNumbers);
	signature = new Signature(invoice.getUser());
	invoiceNo.setValue(invoice.getInvoiceId());
	invoiceDate.setValue(invoice.getInvoiceDate().toLocalDate());
	itemGrid.setItems(invoice.getItems());
	printSignature.setVisible(signature.isSignatureImageExists());
	if (printSignature.isEnabled()) {
	    printSignature.setValue(signature.isSignatureImageExists());
	} else {
	    printSignature.setValue(invoice.getSignImagePath() != null);
	}
    }

    public void setOkVisible(boolean visible) {
	okButton.setVisible(visible);
    }

}

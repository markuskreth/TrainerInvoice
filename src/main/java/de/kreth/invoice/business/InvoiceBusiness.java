package de.kreth.invoice.business;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.kreth.invoice.data.Invoice;
import de.kreth.invoice.data.InvoiceItem;
import de.kreth.invoice.persistence.InvoiceItemRepository;
import de.kreth.invoice.persistence.InvoiceRepository;

@Component
public class InvoiceBusiness extends AbstractBusiness<Invoice> {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceItemRepository itemRepository;

    @Autowired
    public InvoiceBusiness(InvoiceRepository invoiceRepository, InvoiceItemRepository itemRepository) {
	super(invoiceRepository, Invoice.class);
	this.invoiceRepository = invoiceRepository;
	this.itemRepository = itemRepository;
    }

    @Override
    public boolean save(Invoice obj) {

	for (InvoiceItem i : obj.getItems()) {
	    i.setInvoice(obj);
	}
	boolean save = super.save(obj);
	for (InvoiceItem i : obj.getItems()) {
	    itemRepository.save(i);
	}
	return save;
    }

    @Override
    public boolean delete(Invoice obj) {

	for (InvoiceItem i : obj.getItems()) {
	    i.setInvoice(null);
	    itemRepository.save(i);
	}
	return super.delete(obj);
    }

    public String createNextInvoiceId(List<Invoice> invoices, String pattern) {

	Optional<Invoice> latest = invoices.stream()
		.filter(i -> filter(i.getInvoiceId(), pattern))
		.max((o1, o2) -> {
		    return o1.getInvoiceId().compareTo(o2.getInvoiceId());
		});

	int lastInvoiceId = 0;

	if (latest.isPresent()) {
	    String old = latest.get().getInvoiceId();
	    int start = pattern.indexOf("{0}");
	    String substring = old.substring(start);
	    if (substring.matches("[0-9]+")) {
		lastInvoiceId = Integer.parseInt(substring);
	    } else {
		lastInvoiceId++;
	    }
	}

	lastInvoiceId++;
	String invoiceNo = MessageFormat.format(pattern, lastInvoiceId);
	return invoiceNo;
    }

    boolean filter(String invoiceId, String pattern) {

	int start = Math.min(pattern.indexOf("{0}"), invoiceId.length() - 1);
	int end = start + 1;
	while (end < invoiceId.length() && Character.isDigit(invoiceId.charAt(end))) {
	    end++;
	}

	String strippedPattern = pattern.substring(0, start) + pattern.substring(start + 3);
	String strippedIId = invoiceId.substring(0, start) + invoiceId.substring(end);
	return strippedIId.contentEquals(strippedPattern);
    }

    public List<Invoice> findByUserId(long userId) {
	return invoiceRepository.findByUserId(userId);
    }

}

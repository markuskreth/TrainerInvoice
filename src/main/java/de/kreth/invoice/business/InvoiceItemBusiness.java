package de.kreth.invoice.business;

import java.util.Iterator;
import java.util.List;

import org.springframework.stereotype.Component;

import de.kreth.invoice.data.InvoiceItem;
import de.kreth.invoice.data.User;
import de.kreth.invoice.persistence.InvoiceItemRepository;

@Component
public class InvoiceItemBusiness extends AbstractBusiness<InvoiceItem> {

    private final InvoiceItemRepository repository;

    public InvoiceItemBusiness(InvoiceItemRepository invoiceItemRepository) {
	super(invoiceItemRepository, InvoiceItem.class);
	this.repository = invoiceItemRepository;
    }

    public List<InvoiceItem> findByInvoiceIsNull(User user) {
	List<InvoiceItem> findByInvoiceIsNull = repository.findByInvoiceIsNull();
	for (Iterator<InvoiceItem> iterator = findByInvoiceIsNull.iterator(); iterator.hasNext();) {
	    InvoiceItem invoiceItem = iterator.next();
	    if (invoiceItem.getArticle().getUserId() != user.getId()) {
		iterator.remove();
	    }
	}
	return findByInvoiceIsNull;
    }

}

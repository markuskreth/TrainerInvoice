package de.kreth.invoice.persistence;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import de.kreth.invoice.data.InvoiceItem;

public interface InvoiceItemRepository extends CrudRepository<InvoiceItem, Integer> {

    List<InvoiceItem> findByInvoiceIsNull();

}

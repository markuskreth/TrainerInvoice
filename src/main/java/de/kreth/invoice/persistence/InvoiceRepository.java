package de.kreth.invoice.persistence;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import de.kreth.invoice.data.Invoice;

public interface InvoiceRepository extends CrudRepository<Invoice, Long> {

    List<Invoice> findByUserId(long userId);
}

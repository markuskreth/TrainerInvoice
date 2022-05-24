package de.kreth.invoice.persistence;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import de.kreth.invoice.data.Article;
import de.kreth.invoice.data.InvoiceItem;

@Repository
public interface InvoiceItemRepository extends CrudRepository<InvoiceItem, Long> {

    List<InvoiceItem> findByInvoiceIsNull();

    List<InvoiceItem> findByArticle(Article article);
}

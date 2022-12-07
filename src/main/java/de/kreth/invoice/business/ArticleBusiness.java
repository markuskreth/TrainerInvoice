package de.kreth.invoice.business;

import java.util.List;

import org.springframework.stereotype.Component;

import de.kreth.invoice.data.Article;
import de.kreth.invoice.data.InvoiceItem;
import de.kreth.invoice.persistence.ArticleRepository;
import de.kreth.invoice.persistence.InvoiceItemRepository;

@Component
public class ArticleBusiness extends AbstractBusiness<Article> {

    private final ArticleRepository articleRepository;
    private final InvoiceItemRepository invoiceItemRepository;

    public ArticleBusiness(ArticleRepository articleRepository, InvoiceItemRepository invoiceItemRepository) {
	super(articleRepository, Article.class);
	this.articleRepository = articleRepository;
	this.invoiceItemRepository = invoiceItemRepository;
    }

    public boolean hasInvoiceItem(Article current) {
	List<InvoiceItem> items = invoiceItemRepository.findByArticle(current);
	int size = items.size();
	return size > 0;
    }

    public List<Article> findByUserId(Long id) {

	return articleRepository.findByUserId(id);
    }

}

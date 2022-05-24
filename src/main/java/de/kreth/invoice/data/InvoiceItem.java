package de.kreth.invoice.data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoUnit;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "INVOICE_ITEM")
public class InvoiceItem extends BaseEntity {

    private static final long serialVersionUID = 3142997452876778041L;

    private LocalDateTime start;
    private LocalDateTime end;
    @Column(nullable = true, length = 15)
    private String participants;
    @Column(nullable = true, length = 255)
    private String description;

    @ManyToOne(optional = false)
    @JoinColumn(name = "article_id", nullable = false, updatable = false)
    private Article article;

    @ManyToOne(optional = true)
    @JoinColumn(name = "invoice_id", nullable = true, updatable = true)
    private Invoice invoice;

    @Column(name = "sum_price")
    private BigDecimal sumPrice;

    private BigDecimal pricePerHour;

    public String getTitle() {
	return getArticle().getTitle();
    }

    public LocalDateTime getStart() {
	return start;
    }

    public void setStart(LocalDateTime start) {
	this.start = start;
	getSumPrice();
    }

    public LocalDateTime getEnd() {
	return end;
    }

    public void setEnd(LocalDateTime end) {
	this.end = end;
	getSumPrice();
    }

    public String getParticipants() {
	return participants;
    }

    public void setParticipants(String participants) {
	this.participants = participants;
    }

    public Article getArticle() {
	return article;
    }

    public void setArticle(Article article) {
	this.article = article;
	if (article != null) {
	    this.pricePerHour = article.getPricePerHour();
	}
	getSumPrice();
    }

    public BigDecimal getSumPrice() {
	if (article == null || start == null || end == null) {
	    sumPrice = null;
	    return null;
	}

	sumPrice = BigDecimal.valueOf(getDurationInMinutes())
		.setScale(2, RoundingMode.HALF_UP)
		.divide(BigDecimal.valueOf(60), RoundingMode.HALF_UP)
		.multiply(article.getPricePerHour())
		.setScale(2, RoundingMode.HALF_UP);
	return sumPrice;
    }

    public Invoice getInvoice() {
	return invoice;
    }

    public void setInvoice(Invoice invoice) {
	this.invoice = invoice;
    }

    public long getDurationInMinutes() {
	if (start == null || end == null) {
	    return -1L;
	}
	return start.until(end, ChronoUnit.MINUTES);
    }

    public String getDescription() {
	return description;
    }

    public BigDecimal getPricePerHour() {
	if (pricePerHour == null && article != null) {
	    this.pricePerHour = article.getPricePerHour();
	}
	return pricePerHour;
    }

    @Override
    protected String getMediumRepresentation() {
	return DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT).format(start);
    }

    @Override
    public String toString() {
	return "InvoiceItem [id=" + getId() + ", start=" + start + ", end="
		+ end + ", article=" + article + "]";
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((article == null) ? 0 : article.hashCode());
	result = prime * result + ((end == null) ? 0 : end.hashCode());
	result = prime * result + ((start == null) ? 0 : start.hashCode());
	return result;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj) {
	    return true;
	}
	if (obj == null) {
	    return false;
	}
	if (getClass() != obj.getClass()) {
	    return false;
	}
	InvoiceItem other = (InvoiceItem) obj;
	if (article == null) {
	    if (other.article != null) {
		return false;
	    }
	} else if (!article.equals(other.article)) {
	    return false;
	}
	if (end == null) {
	    if (other.end != null) {
		return false;
	    }
	} else if (!end.equals(other.end)) {
	    return false;
	}
	if (start == null) {
	    if (other.start != null) {
		return false;
	    }
	} else if (!start.equals(other.start)) {
	    return false;
	}
	return true;
    }

}

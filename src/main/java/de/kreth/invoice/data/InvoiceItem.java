package de.kreth.invoice.data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "INVOICE_ITEM")
public class InvoiceItem extends BaseEntity {

    private static final long serialVersionUID = 3142997452876778041L;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
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

    @ManyToOne(optional = true)
    @JoinColumn(name = "sportstaette_id", nullable = true, updatable = true)
    private SportStaette sportStaette;

    @ManyToOne(optional = true)
    @JoinColumn(name = "sportart_id", nullable = true, updatable = true)
    private SportArt sportArt;

    @Column(name = "sum_price")
    private BigDecimal sumPrice;

    private BigDecimal pricePerHour;

    public String getTitle() {
	return getArticle().getTitle();
    }

    public LocalDateTime getStart() {
	return startTime;
    }

    public void setStart(LocalDateTime startTime) {
	this.startTime = startTime;
	getSumPrice();
    }

    public LocalDateTime getEnd() {
	return endTime;
    }

    public void setEnd(LocalDateTime endTime) {
	this.endTime = endTime;
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

    public SportStaette getSportStaette() {
	return sportStaette;
    }

    public void setSportStaette(SportStaette sportStaette) {
	this.sportStaette = sportStaette;
    }

    public SportArt getSportArt() {
	return sportArt;
    }

    public void setSportArt(SportArt sportArt) {
	this.sportArt = sportArt;
    }

    public BigDecimal getSumPrice() {
	if (article == null || startTime == null || endTime == null) {
	    sumPrice = null;
	    return BigDecimal.ZERO;
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
	if (startTime == null || endTime == null) {
	    return -1L;
	}
	return startTime.until(endTime, ChronoUnit.MINUTES);
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
	return DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT).format(endTime);
    }

    @Override
    public String toString() {
	return "InvoiceItem [id()=" + getId() + ", start=" + startTime + ", end=" + endTime
		+ ", ort=" + sportStaette + ", Art=" + sportArt + ", article=" + article + "]";
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = super.hashCode();
	result = prime * result + Objects.hash(article, description, endTime, invoice, participants, pricePerHour,
		sportArt, sportStaette, startTime, sumPrice);
	return result;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj) {
	    return true;
	}
	if (!super.equals(obj)) {
	    return false;
	}
	if (getClass() != obj.getClass()) {
	    return false;
	}
	InvoiceItem other = (InvoiceItem) obj;
	return Objects.equals(article, other.article) && Objects.equals(description, other.description)
		&& Objects.equals(endTime, other.endTime) && Objects.equals(invoice, other.invoice)
		&& Objects.equals(participants, other.participants) && Objects.equals(pricePerHour, other.pricePerHour)
		&& Objects.equals(sportArt, other.sportArt) && Objects.equals(sportStaette, other.sportStaette)
		&& Objects.equals(startTime, other.startTime) && Objects.equals(sumPrice, other.sumPrice);
    }

}

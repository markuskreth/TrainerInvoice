package de.kreth.invoice.data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "INVOICE")
public class Invoice extends BaseEntity {

    private static final long serialVersionUID = 736651954892271409L;

    @Column(name = "invoiceid", nullable = false, length = 150)
    private String invoiceId;
    private LocalDateTime invoiceDate;

    private String signImagePath;
    private String reportRessource;

    @OneToMany(mappedBy = "invoice", fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    private List<InvoiceItem> items;

    @ManyToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User user;

    public String getInvoiceId() {
	return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
	this.invoiceId = invoiceId;
    }

    public LocalDateTime getInvoiceDate() {
	return invoiceDate;
    }

    public void setInvoiceDate(LocalDateTime invoiceDate) {
	this.invoiceDate = invoiceDate;
    }

    public List<InvoiceItem> getItems() {
	return items;
    }

    public void setItems(Collection<InvoiceItem> items) {
	this.items = new ArrayList<>(items);
    }

    public User getUser() {
	return user;
    }

    public void setUser(User user) {
	this.user = user;
    }

    public BigDecimal getSum() {
	if (items == null || items.isEmpty()) {
	    return BigDecimal.ZERO;
	}
	return items.stream().map(i -> i.getSumPrice()).reduce(BigDecimal.ZERO,
		BigDecimal::add);
    }

    public String getSignImagePath() {
	return signImagePath;
    }

    public void setSignImagePath(String signImagePath) {
	this.signImagePath = signImagePath;
    }

    public String getReportRessource() {
	return reportRessource;
    }

    public void setReportRessource(String reportRessource) {
	this.reportRessource = reportRessource;
    }

    @Override
    protected String getMediumRepresentation() {
	return invoiceId + " (" + DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).format(invoiceDate) + ")";
    }

    @Override
    public String toString() {
	return "Invoice [invoiceId=" + invoiceId + ", itemscount="
		+ items.size() + ", sum=" + getSum() + "]";
    }

}

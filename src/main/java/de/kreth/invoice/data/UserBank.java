package de.kreth.invoice.data;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "BANKING_CONNECTION")
public class UserBank implements Cloneable, Serializable {

    private static final long serialVersionUID = -7356424394007978241L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID")
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "USER_ID")
    private User user;

    @Column(nullable = false, length = 150)
    private String bankName;
    @Column(nullable = false, length = 150)
    private String iban;
    @Column(nullable = true, length = 150)
    private String bic;

    @Column(name = "created")
    @CreationTimestamp
    private LocalDateTime createdDate;
    @Column(name = "updated")
    @UpdateTimestamp
    private LocalDateTime changeDate;

    public String getBankName() {
	return bankName;
    }

    public void setBankName(String bankName) {
	this.bankName = bankName;
    }

    public String getIban() {
	return iban;
    }

    public void setIban(String iban) {
	this.iban = iban;
    }

    public String getBic() {
	return bic;
    }

    public void setBic(String bic) {
	this.bic = bic;
    }

    @Override
    public String toString() {
	return iban;
    }

    public boolean isValid() {
	return iban != null;
    }

    public User getUser() {
	return user;
    }

    public void setUser(User user) {
	this.user = user;
//	if (user.getBank() == null) {
//	    user.setBank(this);
//	} else {
//	    if (user.getBank().equals(this) == false) {
//		throw new IllegalArgumentException(
//			"User already set, but other than this.");
//	    }
//	}
    }

    public LocalDateTime getCreatedDate() {
	return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
	this.createdDate = createdDate;
    }

    public LocalDateTime getChangeDate() {
	return changeDate;
    }

    public void setChangeDate(LocalDateTime changeDate) {
	this.changeDate = changeDate;
    }

    @PrePersist
    void prePersist() {
	if (this.createdDate == null) {
	    LocalDateTime now = LocalDateTime.now();
	    this.createdDate = now;
	    this.changeDate = now;
	} else {
	    this.changeDate = LocalDateTime.now();
	}
    }

    @Override
    public UserBank clone() {
	UserBank clone = new UserBank();
	clone.setUser(user);
	clone.setBankName(getBankName());
	clone.setBic(getBic());
	clone.setIban(getIban());
	clone.setChangeDate(getChangeDate());
	clone.setCreatedDate(getCreatedDate());
	return clone;
    }

}

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
@Table(name = "ADRESS")
public class UserAdress implements Cloneable, Serializable {

    private static final long serialVersionUID = -8104370538500175340L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID")
    private Long id;

    @Column(nullable = false, length = 255)
    private String adress1;
    @Column(nullable = true, length = 255)
    private String adress2;
    @Column(nullable = true, length = 45)
    private String zip;
    @Column(nullable = true, length = 155)
    private String city;

    @Column(name = "created")
    @CreationTimestamp
    private LocalDateTime createdDate;
    @Column(name = "updated")
    @UpdateTimestamp
    private LocalDateTime changeDate;

    @OneToOne
    @MapsId
    @JoinColumn(name = "USER_ID")
    private User user;

    public User getUser() {
	return user;
    }

    public void setUser(User user) {
	this.user = user;
    }

    public String getAdress1() {
	return adress1;
    }

    public void setAdress1(String adress1) {
	this.adress1 = adress1;
    }

    public String getAdress2() {
	return adress2;
    }

    public void setAdress2(String adress2) {
	this.adress2 = adress2;
    }

    public String getZip() {
	return zip;
    }

    public void setZip(String zip) {
	this.zip = zip;
    }

    public String getCity() {
	return city;
    }

    public void setCity(String city) {
	this.city = city;
    }

    public boolean isValid() {
	return adress1 != null && !adress1.isBlank();
    }

    @Override
    public String toString() {
	return "Adress [adress1=" + adress1 + ", adress2=" + adress2 + ", zip="
		+ zip + ", city=" + city + "]";
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
    public UserAdress clone() {
	UserAdress adress = new UserAdress();
	adress.setAdress1(getAdress1());
	adress.setAdress2(getAdress2());
	adress.setCity(getCity());
	adress.setZip(getZip());
	adress.setUser(getUser());
	adress.setChangeDate(getChangeDate());
	adress.setCreatedDate(getCreatedDate());
	return adress;
    }
}

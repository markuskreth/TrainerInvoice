package de.kreth.invoice.data;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import de.kreth.invoice.views.DataFormat;

@MappedSuperclass
public abstract class BaseEntity implements Serializable {

    private static final long serialVersionUID = 6953593432069408729L;

    @Id
    @GeneratedValue
    private Long id;
    @Column(name = "created")
    @CreationTimestamp
    private LocalDateTime createdDate;
    @Column(name = "updated")
    @UpdateTimestamp
    private LocalDateTime changeDate;

    public Long getId() {
	return id;
    }

    public void setId(long id) {
	this.id = id;
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
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result
		+ ((createdDate == null) ? 0 : createdDate.hashCode());
	result = (int) (prime * result + id);
	return result;
    }

    public String toString(DataFormat format) {
	switch (format) {
	case SMALL:
	    return getSmallRepresentation();
	case MEDIUM:
	    return getMediumRepresentation();
	default:
	    return toString();
	}
    }

    protected String getSmallRepresentation() {
	return String.valueOf(id);
    }

    protected abstract String getMediumRepresentation();

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
	BaseEntity other = (BaseEntity) obj;
	if (createdDate == null) {
	    if (other.createdDate != null) {
		return false;
	    }
	} else if (!createdDate.equals(other.createdDate)) {
	    return false;
	}
	if (id != other.id) {
	    return false;
	}
	return true;
    }

}
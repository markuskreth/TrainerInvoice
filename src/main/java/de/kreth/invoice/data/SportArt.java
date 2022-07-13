package de.kreth.invoice.data;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "SPORTART")
public class SportArt extends BaseEntity {

    private static final long serialVersionUID = 9087707740670720452L;

    @Column(nullable = false, name = "user_id")
    private long userId;

    @Column(nullable = false, length = 50)
    private String name;

    public long getUserId() {
	return userId;
    }

    public void setUserId(long userId) {
	this.userId = userId;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    @Override
    protected String getMediumRepresentation() {
	return name;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = super.hashCode();
	result = prime * result + Objects.hash(name, userId);
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
	SportArt other = (SportArt) obj;
	return Objects.equals(name, other.name) && userId == other.userId;
    }

    @Override
    public String toString() {
	return "SportArt [getId()=" + getId() + ", userId=" + userId + ", name=" + name + "]";
    }

}

package de.kreth.invoice.data;

import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import org.keycloak.representations.AccessToken;

@Entity
@Table(name = "USER")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class User extends BaseEntity {

    private static final long serialVersionUID = -2458030336216327580L;
    private String principalId;
    private String givenName;
    private String familyName;
    private String email;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private UserBank bank;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private UserAdress adress;

    @Column(name = "PRINCIPAL_ID", nullable = false, length = 40, updatable = false, insertable = true, unique = true)
    public String getPrincipalId() {
	return principalId;
    }

    public void setPrincipal(final AccessToken token) {
	final AccessToken accessToken = Objects.requireNonNull(token);
	if (this.principalId != null) {
	    if (!principalId.contentEquals(accessToken.getSubject())) {
		throw new IllegalArgumentException("Non updateable value principalId does not match: this.principalId="
			+ this.principalId + ", token.principalId=" + accessToken.getSubject());
	    }
	}
	principalId = accessToken.getSubject();
	givenName = accessToken.getGivenName();
	familyName = accessToken.getFamilyName();
	email = accessToken.getEmail();
    }

    @Column(nullable = false, length = 50, updatable = false, insertable = true)
    public String getGivenName() {
	return givenName;
    }

    @Column(nullable = false, length = 50, updatable = false, insertable = true)
    public String getFamilyName() {
	return familyName;
    }

    @Column(nullable = false, length = 100, updatable = false, insertable = true)
    public String getEmail() {
	return email;
    }

    public UserBank getBank() {
	return bank;
    }

    public UserAdress getAdress() {
	return adress;
    }

    public void setBank(UserBank bank) {
	if (bank.getUser() != null && !bank.getUser().equals(this)) {
	    throw new IllegalStateException("User of bank is set and not equal to this.");
	}
	bank.setUser(this);
	this.bank = bank;
    }

    public void setAdress(UserAdress adress) {
	if (adress.getUser() != null && !adress.getUser().equals(this)) {
	    throw new IllegalStateException("User of adress is set and not equal to this.");
	}
	adress.setUser(this);
	this.adress = adress;
    }

    @Override
    protected String getMediumRepresentation() {
	return givenName + " " + familyName;
    }
}

package de.kreth.invoice.persistence;

import org.springframework.data.repository.CrudRepository;

import de.kreth.invoice.data.User;
import de.kreth.invoice.data.UserBank;

public interface UserBankRepository extends CrudRepository<UserBank, Long> {

    UserBank findByUser(User user);
}

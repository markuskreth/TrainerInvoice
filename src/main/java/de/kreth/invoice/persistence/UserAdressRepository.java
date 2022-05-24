package de.kreth.invoice.persistence;

import org.springframework.data.repository.CrudRepository;

import de.kreth.invoice.data.User;
import de.kreth.invoice.data.UserAdress;

public interface UserAdressRepository extends CrudRepository<UserAdress, Long> {

    UserAdress findByUser(User user);
}

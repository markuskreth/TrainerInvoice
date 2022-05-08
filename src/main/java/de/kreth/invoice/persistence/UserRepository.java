package de.kreth.invoice.persistence;

import org.springframework.data.repository.CrudRepository;

import de.kreth.invoice.data.User;

public interface UserRepository extends CrudRepository<User, Long> {

    User findByPrincipalId(String tokenSubject);
}

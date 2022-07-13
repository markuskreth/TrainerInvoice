package de.kreth.invoice.persistence;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import de.kreth.invoice.data.SportStaette;

public interface SportstaetteRepository extends CrudRepository<SportStaette, Long> {

    List<SportStaette> findByUserId(long userId);
}

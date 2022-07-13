package de.kreth.invoice.persistence;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import de.kreth.invoice.data.SportArt;

public interface SportArtRepository extends CrudRepository<SportArt, Long> {

    List<SportArt> findByUserId(long userId);
}

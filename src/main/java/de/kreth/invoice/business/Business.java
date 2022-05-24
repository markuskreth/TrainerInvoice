package de.kreth.invoice.business;

import java.util.List;

public interface Business<T> {

    boolean save(T obj);

    boolean delete(T obj);

    List<T> loadAll();

}
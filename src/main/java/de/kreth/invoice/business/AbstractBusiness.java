package de.kreth.invoice.business;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.repository.CrudRepository;

import de.kreth.invoice.data.BaseEntity;

public abstract class AbstractBusiness<T extends BaseEntity> implements Business<T> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Class<T> itemClass;

    private CrudRepository<T, Long> repository;

    public AbstractBusiness(CrudRepository<T, Long> repository, Class<T> itemClass) {
	super();
	this.repository = repository;
	this.itemClass = itemClass;
    }

    @Override
    public boolean save(T obj) {
	repository.save(obj);
	logger.debug("Stored {}", obj);
	return true;
    }

    @Override
    public boolean delete(T obj) {
	repository.delete(obj);
	logger.info("Deleted {}", obj);
	return true;
    }

    @Override
    public List<T> loadAll() {
	List<T> list = new ArrayList<T>();
	repository.findAll().forEach(list::add);
	logger.trace("Loaded {} of {}", list.size(), itemClass.getSimpleName());
	return list;
    }

    public List<T> loadAll(Predicate<T> predicate) {
	List<T> loadAll = loadAll();
	List<T> result = loadAll.stream().filter(predicate).collect(Collectors.toList());
	logger.trace("Filtered {} of {} total {}", result.size(), loadAll.size(), itemClass.getSimpleName());
	return result;
    }

}

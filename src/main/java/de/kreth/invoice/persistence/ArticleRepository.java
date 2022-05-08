package de.kreth.invoice.persistence;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import de.kreth.invoice.data.Article;

public interface ArticleRepository extends CrudRepository<Article, Integer> {

    List<Article> findByUserId(int userId);
}

package de.kreth.invoice.views.article;

import java.text.NumberFormat;
import java.util.ResourceBundle;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.renderer.NumberRenderer;

import de.kreth.invoice.data.Article;

public class ArticleGrid extends Grid<Article> {

    private static final long serialVersionUID = 4063720728031721955L;

    public ArticleGrid(ResourceBundle resBundle) {

	addClassName("bordered");

	addColumn(Article::getTitle).setHeader("Titel");

	NumberRenderer<Article> re = new NumberRenderer<Article>(a -> a.getPricePerHour().doubleValue(),
		NumberFormat.getCurrencyInstance());

	addColumn(re)
		.setHeader("Preis");
	addColumn(Article::getDescription).setHeader("Beschreibung");

    }

}

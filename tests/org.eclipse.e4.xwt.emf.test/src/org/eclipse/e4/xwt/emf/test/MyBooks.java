package org.eclipse.e4.xwt.emf.test;

import org.eclipse.e4.xwt.emf.test.books.Book;
import org.eclipse.e4.xwt.emf.test.books.BooksFactory;
import org.eclipse.e4.xwt.emf.test.books.Bookstore;
import org.eclipse.e4.xwt.emf.test.books.Title;

public class MyBooks {

	private Bookstore bookstore;
	private Book harryPotter;

	public Book getBook() {
		if (harryPotter == null) {
			harryPotter = BooksFactory.eINSTANCE.createBook();
			Title title = BooksFactory.eINSTANCE.createTitle();
			title.setLan("en");
			title.setText("Harry Potter");
			harryPotter.setTitle(title);
			harryPotter.setAuthor("Neal Stephenson");
			harryPotter.setPrice(29.99);
			harryPotter.setYear(2005);
		}
		return harryPotter;
	}

	public Bookstore getBookstore() {
		if (bookstore == null) {
			bookstore = BooksFactory.eINSTANCE.createBookstore();
			bookstore.getBooks().add(getBook());
		}
		return bookstore;
	}
}

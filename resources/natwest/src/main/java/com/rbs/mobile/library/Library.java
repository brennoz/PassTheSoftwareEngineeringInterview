package com.rbs.mobile.library;

import java.util.ArrayList;
import java.util.List;

public class Library {

	private final List<Book> books = new ArrayList<>();

	public List<Book> search(String searchString) {
		return books;
	}

	public void add(Book book) {
		books.add(book);
	}
}

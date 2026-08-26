package com.interview.ood.q042_library_search.answer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Library {
    
    private List<Book> books = new ArrayList<>();

    public void add(Book book) {
        books.add(book);
    }

    public List<Book> search(String searchString) {
        BookSpecification spec = new OrSpecification(
                new TitleContainsSpecification(searchString),
                new AuthorContainsSpecification(searchString));
        return books.stream()
                .filter(spec::isSatisfiedBy)
                .collect(Collectors.toList());
    }
}

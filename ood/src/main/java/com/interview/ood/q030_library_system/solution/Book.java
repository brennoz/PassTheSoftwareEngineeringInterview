package com.interview.ood.q030_library_system.solution;

// Why status lives on BookCopy, not Book: a library can hold several physical copies of the
// same title (multiple barcodes, one ISBN), and each copy is independently on the shelf,
// reserved, borrowed, overdue, or lost. Book itself (isbn/title/author) never changes —
// putting mutable status here would conflate "what the book is" with "where a specific
// physical copy currently is." Same reasoning as this course's other library exercise
// (interview-prep/natwest/ex4_library_borrowing).
class Book {
    private final String isbn;
    private final String title;
    private final String author;

    Book(String isbn, String title, String author) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
    }

    public String getIsbn()   { return isbn; }
    public String getTitle()  { return title; }
    public String getAuthor() { return author; }
}

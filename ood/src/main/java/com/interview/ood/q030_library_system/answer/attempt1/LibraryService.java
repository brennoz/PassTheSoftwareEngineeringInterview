package com.interview.ood.q030_library_system.answer.attempt1;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// ─── State interface ────────────────────────────────────────────────────────

interface CopyState {
    // TODO 1: implement the four transition methods + name()
    void reserve(BookCopy copy);
    void checkout(BookCopy copy);
    void returnCopy(BookCopy copy);
    void markLost(BookCopy copy);
    String name();
}

// ─── Concrete States ────────────────────────────────────────────────────────

class AvailableState implements CopyState {
    // TODO 2: implement reserve() → setState(ReservedState), checkout() → setState(BorrowedState),
    //         returnCopy() → throw, markLost() → throw, name() → "AVAILABLE"
    @Override public void reserve(BookCopy c)    {
        /* TODO */
        c.setState(new ReservedState());
    }
    @Override public void checkout(BookCopy c)   {
        /* TODO */
        c.setState(new BorrowedState());
    }
    @Override public void returnCopy(BookCopy c) { throw new IllegalStateException("Not borrowed"); }
    @Override public void markLost(BookCopy c)   { throw new IllegalStateException("Not borrowed"); }
    @Override public String name()               { return "AVAILABLE"; }
}

class ReservedState implements CopyState {
    // TODO 3: reserve() → throw, checkout() → setState(BorrowedState),
    //         returnCopy() → setState(AvailableState), markLost() → throw, name() → "RESERVED"
    @Override public void reserve(BookCopy c)    { throw new IllegalStateException("Already reserved"); }
    @Override public void checkout(BookCopy c)   {
        /* TODO */
        c.setState(new BorrowedState());
    }
    @Override public void returnCopy(BookCopy c) {
        /* TODO */
        c.setState(new AvailableState());
    }
    @Override public void markLost(BookCopy c)   { throw new IllegalStateException("Not borrowed"); }
    @Override public String name()               { return "RESERVED"; }
}

class BorrowedState implements CopyState {
    // TODO 4: reserve() → throw, checkout() → throw,
    //         returnCopy() → setState(AvailableState), markLost() → setState(LostState), name() → "BORROWED"
    @Override public void reserve(BookCopy c)    { throw new IllegalStateException("Already borrowed"); }
    @Override public void checkout(BookCopy c)   { throw new IllegalStateException("Already borrowed"); }
    @Override public void returnCopy(BookCopy c) {
        /* TODO */
        c.setState(new AvailableState());
    }
    @Override public void markLost(BookCopy c)   {
        /* TODO */
        c.setState(new LostState());
    }
    @Override public String name()               { return "BORROWED"; }
}

class OverdueState implements CopyState {
    // TODO 5: reserve() → throw, checkout() → throw,
    //         returnCopy() → setState(AvailableState), markLost() → setState(LostState), name() → "OVERDUE"
    @Override public void reserve(BookCopy c)    { throw new IllegalStateException("Copy is overdue"); }
    @Override public void checkout(BookCopy c)   { throw new IllegalStateException("Copy is overdue"); }
    @Override public void returnCopy(BookCopy c) {
        /* TODO */
        c.setState(new AvailableState());
    }
    @Override public void markLost(BookCopy c)   {
        /* TODO */
        c.setState(new LostState());
    }
    @Override public String name()               { return "OVERDUE"; }
}

class LostState implements CopyState {
    // TODO 6: terminal state — all four transitions throw, name() → "LOST"
    @Override public void reserve(BookCopy c)    { throw new IllegalStateException("Copy is lost"); }
    @Override public void checkout(BookCopy c)   { throw new IllegalStateException("Copy is lost"); }
    @Override public void returnCopy(BookCopy c) { throw new IllegalStateException("Copy is lost"); }
    @Override public void markLost(BookCopy c)   { throw new IllegalStateException("Already lost"); }
    @Override public String name()               { return "LOST"; }
}

// ─── Fine strategies ────────────────────────────────────────────────────────

interface FineCalculationStrategy {
    // TODO 7: declare BigDecimal calculate(Loan loan)
    BigDecimal calculate(Loan loan);
}

class DailyFineStrategy implements FineCalculationStrategy {
    private final BigDecimal ratePerDay;

    DailyFineStrategy(BigDecimal ratePerDay) { this.ratePerDay = ratePerDay; }

    @Override
    public BigDecimal calculate(Loan loan) {
        // TODO 8: days overdue = today - dueDate (if <= 0 return ZERO)
        //         return ratePerDay * daysOverdue  (BigDecimal.multiply + valueOf)
        long overdue = ChronoUnit.DAYS.between(loan.getDueDate(), LocalDate.now());
        return (overdue > 0) ? ratePerDay.multiply(BigDecimal.valueOf(overdue)) : BigDecimal.ZERO;
    }
}

class FlatFineStrategy implements FineCalculationStrategy {
    private final BigDecimal flatAmount;

    FlatFineStrategy(BigDecimal flatAmount) { this.flatAmount = flatAmount; }

    @Override
    public BigDecimal calculate(Loan loan) {
        // TODO 9: if overdue return flatAmount else return ZERO
        long overdue = ChronoUnit.DAYS.between(loan.getDueDate(), LocalDate.now());
        return (overdue > 0) ? flatAmount : BigDecimal.ZERO;
    }
}

// ─── Domain entities ────────────────────────────────────────────────────────

class Book {
    private final String isbn;
    private final String title;
    private final String author;

    Book(String isbn, String title, String author) {
        this.isbn   = isbn;
        this.title  = title;
        this.author = author;
    }

    public String getIsbn()   { return isbn; }
    public String getTitle()  { return title; }
    public String getAuthor() { return author; }
}

class BookCopy {
    private final String barcode;
    private final Book   book;
    private CopyState    state = new AvailableState();

    BookCopy(String barcode, Book book) {
        this.barcode = barcode;
        this.book    = book;
    }

    // TODO 10: implement the four delegation methods below by calling state.*
    public void reserve()    {
        /* TODO */
        state.reserve(this);
    }
    public void checkout()   {
        /* TODO */
        state.checkout(this);
    }
    public void returnCopy() {
        /* TODO */
        state.returnCopy(this);
    }
    public void markLost()   {
        /* TODO */
        state.markLost(this);
    }

    void setState(CopyState s) { this.state = s; }
    public String getStatus()  { return state.name(); }  // TODO: state.name() once CopyState.name() exists

    public String getBarcode() { return barcode; }
    public Book   getBook()    { return book; }
}

class Member {
    private static final int MAX_LOANS = 3;

    private final String      id;
    private final String      name;
    private final List<Loan>  activeLoans = new ArrayList<>();

    Member(String id, String name) {
        this.id   = id;
        this.name = name;
    }

    public boolean hasReachedBorrowLimit() { return activeLoans.size() >= MAX_LOANS; }
    public void    addActiveLoan(Loan loan) { activeLoans.add(loan); }
    public void    removeLoan(Loan loan)    { activeLoans.remove(loan); }
    public String  getId()                  { return id; }
    public String  getName()                { return name; }
    public List<Loan> getActiveLoans()      { return List.copyOf(activeLoans); }
}

class Loan {
    private final String    id;
    private final Member    member;
    private final BookCopy  copy;
    private final LocalDate borrowedOn;
    private final LocalDate dueDate;
    private LocalDate       returnedOn;

    Loan(Member member, BookCopy copy, int loanDays) {
        this.id         = UUID.randomUUID().toString();
        this.member     = member;
        this.copy       = copy;
        this.borrowedOn = LocalDate.now();
        this.dueDate    = borrowedOn.plusDays(loanDays);
    }

    public boolean isOverdue()   { return returnedOn == null && LocalDate.now().isAfter(dueDate); }
    void setReturnedOn(LocalDate d) { this.returnedOn = d; }
    public LocalDate getDueDate() { return dueDate; }
    public Member    getMember()  { return member; }
    public BookCopy  getCopy()    { return copy; }
    public String    getId()      { return id; }
}

class Fine {
    private final Loan       loan;
    private final BigDecimal amount;
    private boolean          paid = false;

    Fine(Loan loan, BigDecimal amount) {
        this.loan   = loan;
        this.amount = amount;
    }

    public void     pay()          { this.paid = true; }
    public boolean  isPaid()       { return paid; }
    public BigDecimal getAmount()  { return amount; }
    public Loan     getLoan()      { return loan; }
}

// ─── Service ─────────────────────────────────────────────────────────────────

class BorrowLimitExceededException extends RuntimeException {
    BorrowLimitExceededException(String memberId) {
        super("Member " + memberId + " has reached the borrow limit");
    }
}

public class LibraryService {

    private final FineCalculationStrategy fineStrategy;

    public LibraryService(FineCalculationStrategy fineStrategy) {
        this.fineStrategy = fineStrategy;
    }

    public Loan checkout(Member member, BookCopy copy, int loanDays) {
        // TODO 11: check borrow limit, then call copy.checkout(), create Loan,
        //          add to member's active loans, return loan
        if (member.hasReachedBorrowLimit()) {
            throw new BorrowLimitExceededException(member.getId());
        }
        copy.checkout();
        Loan loan = new Loan(member, copy, loanDays);
        member.addActiveLoan(loan);
        return loan;
        //throw new UnsupportedOperationException("TODO 11 — implement checkout");
    }

    public Fine returnBook(Member member, Loan loan) {
        // TODO 12: set returnedOn on loan, call copy.returnCopy(), remove loan from member,
        //          calculate fine via strategy, return Fine or null if amount is zero
        loan.setReturnedOn(LocalDate.now());
        loan.getCopy().returnCopy();
        member.removeLoan(loan);
        BigDecimal fine = fineStrategy.calculate(loan);
        //throw new UnsupportedOperationException("TODO 12 — implement returnBook");
        return fine.compareTo(BigDecimal.ZERO) > 0 ? new Fine(loan, fine) : null;
    }
}

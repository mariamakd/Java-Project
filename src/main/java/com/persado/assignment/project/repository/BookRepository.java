package com.persado.assignment.project.repository;

import com.persado.assignment.project.model.Book;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author maria
 */
public interface BookRepository extends JpaRepository<Book, String> {

    @Query("SELECT b FROM Book b where b.copiesForLoan>0 ORDER by b.title")
    public List<Book> findAllAvailable();

    @Query("SELECT b FROM Book b where b.copies!=b.copiesForLoan ORDER by b.title")
    public List<Book> findAllOnLoan();

    @Query("SELECT b FROM UserBook ub JOIN ub.book b where ub.user.id=:userId ORDER BY ub.loanDate")
    public List<Book> findBooksByUserHistorically(@Param("userId") Long userId);

    @Query("SELECT b FROM UserBook ub JOIN ub.book b where ub.onloan=1 AND b.isbn=:isbn")
    public Optional<Book> findbookOnLoan(@Param("isbn") String isbn);

}

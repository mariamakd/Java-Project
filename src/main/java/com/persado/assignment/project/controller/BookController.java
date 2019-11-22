package com.persado.assignment.project.controller;

import com.persado.assignment.project.dto.BookDto;
import com.persado.assignment.project.dto.UserDto;
import com.persado.assignment.project.service.BookService;
import com.persado.assignment.project.service.UserService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author maria
 */
@Controller
@RequestMapping("/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @Autowired
    private UserService userService;

    @GetMapping("/list")
    public String getBookList(Model theModel) {
        List<BookDto> bookList = bookService.findAll();
        theModel.addAttribute("books", bookList);
        theModel.addAttribute("action", "list");
        return "/book/book-list";
    }

    @GetMapping("/add-book")
    public String goToBookCreateForm(Model model) {
        BookDto bookDto = new BookDto();
        model.addAttribute("book", bookDto);
        return "/book/book-create";
    }

    @PostMapping("/create")
    public String createBook(@ModelAttribute("book") BookDto bookDto) {
        bookService.save(bookDto);
        return "redirect:/books/list";
    }

    @GetMapping("delete/{isbn}")
    public String deleteBook(@PathVariable("isbn") String isbn) {
        bookService.deleteById(isbn);
        return "redirect:/books/list";
    }

    @GetMapping("/list/loan")
    public String getBookListForLoan(Model model) {
        List<BookDto> books = bookService.findAllAvailable();
        for (BookDto book : books) {
            book.setUsersAvailForLoan(userService.getUsersAvailableForLoan(book.getIsbn()));
        }
        model.addAttribute("books", books);
        model.addAttribute("action", "loan");
        model.addAttribute("userDto", new UserDto());
        return "/book/book-loan-return";
    }

    @GetMapping("/list/return")
    public String getBookListForReturn(Model model) {
        model.addAttribute("books", bookService.findAllOnLoan());
        model.addAttribute("action", "return");
        model.addAttribute("userDto", new UserDto());
        return "/book/book-loan-return";
    }

    @PutMapping("/return/{isbn}")
    public String returnBook(@ModelAttribute("userDto") UserDto user, @PathVariable("isbn") String isbn) {
        bookService.returnBook(isbn, user.getId());
        return "redirect://book/book-loan-return";
    }

    @PutMapping("/loan/{isbn}")
    public String loanBook(@ModelAttribute("userDto") UserDto user, @PathVariable("isbn") String isbn) {
        if (user.getId() == null) {
            return "User id can not be null";
        }
        if (userService.lonedBooksByUser(user.getId()) >= 3) {
            return "This user has reached max book loan";
        }
        bookService.loanBook(isbn, user.getId());

        return "redirect:/book/book-loan-return";
    }

}
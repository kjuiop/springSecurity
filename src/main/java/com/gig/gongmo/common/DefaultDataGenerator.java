package com.gig.gongmo.common;

import com.gig.gongmo.account.Account;
import com.gig.gongmo.account.AccountService;
import com.gig.gongmo.book.Book;
import com.gig.gongmo.book.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.data.repository.query.SecurityEvaluationContextExtension;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DefaultDataGenerator implements ApplicationRunner {

    private final AccountService accountService;
    private final BookRepository bookRepository;



    @Override
    public void run(ApplicationArguments args) throws Exception {
        // TODO jake - spring
        // TODO jay -jpa

        Account jake = createUser("jake");
        Account jay = createUser("jay");

        Book spring = createBook("spring", jake);
        Book jpa = createBook("jpa", jay);
    }

    private Book createBook(String title, Account jake) {
        Book book = new Book();
        book.setTitle(title);
        book.setAuthor(jake);
        return bookRepository.save(book);
    }

    private Account createUser(String username) {
        Account account = new Account();
        account.setUsername(username);
        account.setPassword("123");
        account.setRole("USER");
        return accountService.createNew(account);
    }
}

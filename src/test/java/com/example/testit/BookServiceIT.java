package com.example.testit;

import com.example.testit.model.Book;
import com.example.testit.model.Member;
import com.example.testit.model.Status;
import com.example.testit.repository.BookRepository;
import com.example.testit.repository.MemberRepository;
import com.example.testit.service.BookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BookServiceIT {

    @Autowired
    BookService bookService;
    @Autowired
    BookRepository bookRepository;
    @Autowired
    MemberRepository memberRepository;

    @Test
    void emprunterPuisRendreUnLivre() {
        // given
        Member alice = memberRepository.save(new Member("alice_service"));
        Book livre = bookService.createBook("Dune", "Herbert");

        // when
        bookService.borrowBook(livre.getId(), alice.getId());
        bookService.returnBook(livre.getId(), alice.getId());

        // then
        Book relu = bookRepository.findById(livre.getId()).get();
        assertEquals(Status.RENDU, relu.getStatus());
        assertEquals(alice.getId(), relu.getBorrower().getId());
    }
}

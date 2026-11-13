package com.example.testit;

import com.example.testit.model.Book;
import com.example.testit.model.Member;
import com.example.testit.model.Status;
import com.example.testit.repository.BookRepository;
import com.example.testit.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BookRepositoryIT {

    @Autowired
    BookRepository bookRepository;
    @Autowired
    MemberRepository memberRepository;

    @Test
    void findByMemberAndStatusAvecDeuxMembres() {
        // given
        Member alice = memberRepository.save(new Member("alice"));
        Member bob = memberRepository.save(new Member("bob"));

        Book b1 = new Book("Dune", "Herbert");
        b1.setStatus(Status.EMPRUNTE);
        b1.setBorrower(alice);
        bookRepository.save(b1);

        Book b2 = new Book("Fondation", "Asimov");
        b2.setStatus(Status.RENDU);
        b2.setBorrower(alice);
        bookRepository.save(b2);

        Book b3 = new Book("Hyperion", "Simmons");
        b3.setStatus(Status.EMPRUNTE);
        b3.setBorrower(bob);
        bookRepository.save(b3);

        // when
        List<Book> result = bookRepository.findByMemberAndStatus(alice.getId(), Status.EMPRUNTE);

        // then
        assertEquals(1, result.size());
        assertEquals("Dune", result.get(0).getTitle());
    }
}

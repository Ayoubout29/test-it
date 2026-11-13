package com.example.testit;

import com.example.testit.adapter.mail.MailService;
import com.example.testit.model.Book;
import com.example.testit.model.Member;
import com.example.testit.model.Status;
import com.example.testit.repository.BookRepository;
import com.example.testit.repository.MemberRepository;
import com.example.testit.service.BookService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    BookRepository bookRepository;
    @Mock
    MemberRepository memberRepository;
    @Mock
    MailService mailService;

    @InjectMocks
    BookService bookService;

    @Test
    void emprunterUnLivreDisponible() {
        // given
        Book livre = new Book("Dune", "Herbert");
        livre.setId(1L);
        Member alice = new Member("alice");
        alice.setId(10L);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(livre));
        when(memberRepository.findById(10L)).thenReturn(Optional.of(alice));
        when(bookRepository.findByMemberAndStatus(10L, Status.EMPRUNTE)).thenReturn(List.of());
        when(bookRepository.save(any())).thenReturn(livre);

        // when
        Book result = bookService.borrowBook(1L, 10L);

        // then
        assertEquals(Status.EMPRUNTE, result.getStatus());
        assertEquals(alice, result.getBorrower());
        verify(mailService).sendMail(eq(alice), anyString(), anyString());
    }

    @Test
    void refuseLEmpruntSiLeLivreEstDejaEmprunte() {
        Book livre = new Book("Dune", "Herbert");
        livre.setStatus(Status.EMPRUNTE);
        Member alice = new Member("alice");
        when(bookRepository.findById(1L)).thenReturn(Optional.of(livre));
        when(memberRepository.findById(10L)).thenReturn(Optional.of(alice));

        assertThrows(IllegalStateException.class, () -> bookService.borrowBook(1L, 10L));
    }

    @Test
    void refuseLEmpruntSiLeMembreADejaUnLivre() {
        Book livre = new Book("Dune", "Herbert");
        Member alice = new Member("alice");
        when(bookRepository.findById(1L)).thenReturn(Optional.of(livre));
        when(memberRepository.findById(10L)).thenReturn(Optional.of(alice));
        when(bookRepository.findByMemberAndStatus(10L, Status.EMPRUNTE)).thenReturn(List.of(new Book()));

        assertThrows(IllegalStateException.class, () -> bookService.borrowBook(1L, 10L));
    }

    @Test
    void rendreUnLivrePrevientLeReferent() {
        Member referent = new Member("biblio");
        Member alice = new Member("alice");
        alice.setId(10L);
        alice.setReferent(referent);
        Book livre = new Book("Dune", "Herbert");
        livre.setStatus(Status.EMPRUNTE);
        livre.setBorrower(alice);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(livre));

        bookService.returnBook(1L, 10L);

        verify(mailService).sendMail(eq(alice), anyString(), anyString());
        verify(mailService).sendMail(eq(referent), anyString(), anyString());
    }

    @Test
    void unAutreMembreNePeutPasRendreLeLivre() {
        Member alice = new Member("alice");
        alice.setId(10L);
        Book livre = new Book("Dune", "Herbert");
        livre.setStatus(Status.EMPRUNTE);
        livre.setBorrower(alice);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(livre));

        assertThrows(IllegalStateException.class, () -> bookService.returnBook(1L, 20L));
    }
}

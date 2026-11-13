package com.example.testit;

import com.example.testit.adapter.member.CurrentMemberServiceFake;
import com.example.testit.model.Member;
import com.example.testit.repository.MemberRepository;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class BookControllerIT {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    CurrentMemberServiceFake currentMemberService;
    @Autowired
    MemberRepository memberRepository;

    @Test
    void creerUnLivrePuisLEmprunter() throws Exception {
        Member bob = memberRepository.save(new Member("bob_controller"));
        currentMemberService.setCurrent(bob.getId());

        // creation du livre
        String json = mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Dune\",\"author\":\"Herbert\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Dune"))
                .andExpect(jsonPath("$.author").value("Herbert"))
                .andReturn().getResponse().getContentAsString();
        Integer id = JsonPath.read(json, "$.id");

        // emprunt
        mockMvc.perform(post("/books/" + id + "/borrow"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("EMPRUNTE"));

        currentMemberService.setCurrent(null);
    }

    @Test
    void emprunterSansEtreConnecte() throws Exception {
        currentMemberService.setCurrent(null);

        mockMvc.perform(post("/books/1/borrow"))
                .andExpect(status().isBadRequest());
    }
}

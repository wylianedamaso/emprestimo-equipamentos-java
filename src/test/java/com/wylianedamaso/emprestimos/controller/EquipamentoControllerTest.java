package com.wylianedamaso.emprestimos.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class EquipamentoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveCadastrarEquipamentoRetornando201() throws Exception {
        String json = """
                {
                    "patrimonio": "TEST-001",
                    "nome": "Notebook Dell",
                    "tipo": "NOTEBOOK"
                }
                """;

        mockMvc.perform(post("/equipamentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.disponivel").value(true));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveRetornar404AoBuscarEquipamentoInexistente() throws Exception {
        mockMvc.perform(get("/equipamentos/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRetornar401SemAutenticacao() throws Exception {
        mockMvc.perform(get("/equipamentos"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "COLABORADOR")
    void deveRetornar403QuandoColaboradorTentaCadastrarEquipamento() throws Exception {
        String json = """
                {
                    "patrimonio": "TEST-403",
                    "nome": "Mouse Sem Fio",
                    "tipo": "MOUSE"
                }
                """;

        mockMvc.perform(post("/equipamentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isForbidden());
    }
}
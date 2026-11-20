package com.wylianedamaso.emprestimos.controller;

import com.wylianedamaso.emprestimos.model.Colaborador;
import com.wylianedamaso.emprestimos.model.Equipamento;
import com.wylianedamaso.emprestimos.model.TipoEquipamento;
import com.wylianedamaso.emprestimos.repository.ColaboradorRepository;
import com.wylianedamaso.emprestimos.repository.EquipamentoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.jayway.jsonpath.JsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class EmprestimoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EquipamentoRepository equipamentoRepository;

    @Autowired
    private ColaboradorRepository colaboradorRepository;

    private Equipamento salvarEquipamento(String patrimonio) {
        Equipamento e = new Equipamento();
        e.setPatrimonio(patrimonio);
        e.setNome("Monitor LG");
        e.setTipo(TipoEquipamento.MONITOR);
        return equipamentoRepository.save(e);
    }

    private Colaborador salvarColaborador(String email) {
        Colaborador c = new Colaborador();
        c.setNome("Ana Souza");
        c.setEmail(email);
        c.setCargo("Analista");
        return colaboradorRepository.save(c);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveEmprestarEDevolverEquipamentoRetornando200() throws Exception {
        Equipamento equipamento = salvarEquipamento("TEST-EMP-01");
        Colaborador colaborador = salvarColaborador("ana.teste1@empresa.com");

        String resposta = mockMvc.perform(post("/emprestimos")
                        .param("equipamentoId", equipamento.getId().toString())
                        .param("colaboradorId", colaborador.getId().toString()))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        // Extrai o id do JSON retornado para testar a devolução
        Integer id = JsonPath.read(resposta, "$.id");

        mockMvc.perform(put("/emprestimos/" + id + "/devolucao"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dataDevolucao").isNotEmpty());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveRetornar409AoEmprestarEquipamentoJaEmprestado() throws Exception {
        Equipamento equipamento = salvarEquipamento("TEST-EMP-02");
        Colaborador colaborador = salvarColaborador("ana.teste2@empresa.com");

        mockMvc.perform(post("/emprestimos")
                        .param("equipamentoId", equipamento.getId().toString())
                        .param("colaboradorId", colaborador.getId().toString()))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/emprestimos")
                        .param("equipamentoId", equipamento.getId().toString())
                        .param("colaboradorId", colaborador.getId().toString()))
                .andExpect(status().isConflict());
    }
}
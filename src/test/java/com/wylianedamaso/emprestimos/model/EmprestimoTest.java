package com.wylianedamaso.emprestimos.model;

import com.wylianedamaso.emprestimos.exception.ConflitoException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class EmprestimoTest {
    @Test
    void criarEmprestimoCalculaPrazoDeQuatorzeDias() {
        LocalDate hoje = LocalDate.of(2026, 11, 14);
        Emprestimo emprestimo = new Emprestimo(new Equipamento(), new Colaborador(), hoje);

        assertEquals(LocalDate.of(2026, 11, 14), emprestimo.getDataEmprestimo());
        assertEquals(LocalDate.of(2026, 11, 28), emprestimo.getDataPrevistaDevolucao());
    }

    @Test
    void devolverEmprestimoAbertoRegistraDataDeDevolucao() {
        Emprestimo emprestimo = new Emprestimo(new Equipamento(), new Colaborador(), LocalDate.of(2026, 11, 14));

        emprestimo.devolver(LocalDate.of(2026, 11, 20));

        assertEquals(LocalDate.of(2026, 11, 20), emprestimo.getDataDevolucao());
    }

    @Test
    void devolverEmprestimoDuasVezesLancaExcecao() {
        Emprestimo emprestimo = new Emprestimo(new Equipamento(), new Colaborador(), LocalDate.of(2026, 11, 14));
        emprestimo.devolver(LocalDate.of(2026, 11, 20));

        assertThrows(ConflitoException.class, () -> emprestimo.devolver(LocalDate.of(2026, 11, 21)));
    }

    @Test
    void emprestimoVencidoENaoDevolvidoEstaAtrasado() {
        Emprestimo emprestimo = new Emprestimo(new Equipamento(), new Colaborador(), LocalDate.of(2026, 10, 27)); // prazo: 10/11

        assertTrue(emprestimo.estaAtrasadoEm(LocalDate.of(2026, 11, 14)));
    }

    @Test
    void emprestimoDentroDoPrazoNaoEstaAtrasado() {
        Emprestimo emprestimo = new Emprestimo(new Equipamento(), new Colaborador(), LocalDate.of(2026, 11, 6)); // prazo: 20/11

        assertFalse(emprestimo.estaAtrasadoEm(LocalDate.of(2026, 11, 14)));
    }

    @Test
    void emprestimoJaDevolvidoNaoEstaAtrasado() {
        Emprestimo emprestimo = new Emprestimo(new Equipamento(), new Colaborador(), LocalDate.of(2026, 10, 27)); // prazo: 10/11
        emprestimo.devolver(LocalDate.of(2026, 11, 12));

        assertFalse(emprestimo.estaAtrasadoEm(LocalDate.of(2026, 11, 14)));
    }

    @Test
    void emprestimoNoDiaDoPrazoAindaNaoEstaAtrasado() {
        Emprestimo emprestimo = new Emprestimo(new Equipamento(), new Colaborador(), LocalDate.of(2026, 10, 31)); // prazo: 14/11

        assertFalse(emprestimo.estaAtrasadoEm(LocalDate.of(2026, 11, 14)));
    }
}

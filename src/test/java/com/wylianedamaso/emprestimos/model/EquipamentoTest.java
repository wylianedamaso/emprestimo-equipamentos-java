package com.wylianedamaso.emprestimos.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EquipamentoTest {

    @Test
    void emprestarEquipamentoDisponivelFicaIndisponivel() {
        Equipamento equipamento = new Equipamento();

        equipamento.emprestar();

        assertFalse(equipamento.isDisponivel());
    }

    @Test
    void emprestarEquipamentoIndisponivelLancaExcecao() {
        Equipamento equipamento = new Equipamento();
        equipamento.emprestar();

        assertThrows(IllegalStateException.class, () -> equipamento.emprestar());
    }

    @Test
    void devolverEquipamentoFicaDisponivelNovamente() {
        Equipamento equipamento = new Equipamento();
        equipamento.emprestar();

        equipamento.devolver();

        assertTrue(equipamento.isDisponivel());
    }
}

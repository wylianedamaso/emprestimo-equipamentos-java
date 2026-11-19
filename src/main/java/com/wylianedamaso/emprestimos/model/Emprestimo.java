package com.wylianedamaso.emprestimos.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class Emprestimo {

    private static final int PRAZO_EM_DIAS = 14;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long equipamentoId;

    @Column(nullable = false)
    private Long colaboradorId;

    @Column(nullable = false)
    private LocalDate dataEmprestimo;

    @Column(nullable = false)
    private LocalDate dataPrevistaDevolucao;

    private LocalDate dataDevolucao; //null até retornar

    public Emprestimo(){

    }

    public Emprestimo(Long equipamentoId, Long colaboradorId, LocalDate hoje){
        this.equipamentoId = equipamentoId;
        this.colaboradorId = colaboradorId;
        this.dataEmprestimo = hoje;
        this.dataPrevistaDevolucao = hoje.plusDays(PRAZO_EM_DIAS);

    }

    public void devolver(LocalDate data){
        if(this.dataDevolucao != null) {
            throw new IllegalStateException("Empréstimo ja devolvido");
        }
        this.dataDevolucao = data;

    }

    public boolean estaAtrasadoEm(LocalDate data){
        return this.dataDevolucao == null && this.dataPrevistaDevolucao.isBefore(data);
    }

    public boolean isAtrasado(){
        return estaAtrasadoEm(LocalDate.now());
    }

    public Long getId() {
        return id;
    }

    public Long getEquipamentoId() {
        return equipamentoId;
    }

    public Long getColaboradorId() {
        return colaboradorId;
    }

    public LocalDate getDataEmprestimo() {
        return dataEmprestimo;
    }

    public LocalDate getDataPrevistaDevolucao() {
        return dataPrevistaDevolucao;
    }

    public LocalDate getDataDevolucao() {
        return dataDevolucao;
    }
}

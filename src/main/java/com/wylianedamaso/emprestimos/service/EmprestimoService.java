package com.wylianedamaso.emprestimos.service;

import com.wylianedamaso.emprestimos.exception.ConflitoException;
import com.wylianedamaso.emprestimos.exception.NaoEncontradoException;
import com.wylianedamaso.emprestimos.model.Colaborador;
import com.wylianedamaso.emprestimos.model.Emprestimo;
import com.wylianedamaso.emprestimos.model.Equipamento;
import com.wylianedamaso.emprestimos.repository.ColaboradorRepository;
import com.wylianedamaso.emprestimos.repository.EmprestimoRepository;
import com.wylianedamaso.emprestimos.repository.EquipamentoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
public class EmprestimoService {
    private final EmprestimoRepository repository;
    private final EquipamentoRepository equipamentoRepository;
    private final ColaboradorRepository colaboradorRepository;


    public EmprestimoService(EmprestimoRepository repository, EquipamentoRepository equipamentoRepository, ColaboradorRepository colaboradorRepository){
        this.repository = repository;
        this.equipamentoRepository = equipamentoRepository;
        this.colaboradorRepository = colaboradorRepository;
    }

    public List<Emprestimo> listar(Boolean atrasados){
        if(Boolean.TRUE.equals(atrasados)) {
            return repository.findByDataDevolucaoIsNullAndDataPrevistaDevolucaoBefore(LocalDate.now());
        }
        return repository.findAll();
    }

    public Emprestimo buscar(Long id){
        return repository.findById(id).orElseThrow(() -> new NaoEncontradoException("Empréstimo não encontrado"));
    }

    public List<Emprestimo> listarDoColaborador(Long colaboradorId) {
        if (!colaboradorRepository.existsById(colaboradorId)){
            throw new NaoEncontradoException("Colaborador não encontrado");
        }
        return repository.findByColaboradorId(colaboradorId);
    }

    @Transactional
    public Emprestimo emprestar(Long equipamentoId, Long colaboradorId) {
        Equipamento equipamento = equipamentoRepository.findById(equipamentoId)
                .orElseThrow(() -> new NaoEncontradoException("Equipamento não encontrado"));

        Colaborador colaborador = colaboradorRepository.findById(colaboradorId)
                .orElseThrow(() -> new NaoEncontradoException("Colaborador não encontrado"));

        if (repository.existsByColaboradorIdAndDataDevolucaoIsNullAndDataPrevistaDevolucaoBefore(
                colaboradorId, LocalDate.now())) {
            throw new ConflitoException("Colaborador possui empréstimo atrasado");
        }

        equipamento.emprestar();
        equipamentoRepository.save(equipamento);

        Emprestimo emprestimo = new Emprestimo(equipamento, colaborador, LocalDate.now());
        return repository.save(emprestimo);
    }

    @Transactional
    public Emprestimo devolver(Long id) {
        Emprestimo emprestimo = buscar(id);
        emprestimo.devolver(LocalDate.now());

        Equipamento equipamento = emprestimo.getEquipamento();
        equipamento.devolver();
        equipamentoRepository.save(equipamento);

        return repository.save(emprestimo);
    }
}


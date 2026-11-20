package com.wylianedamaso.emprestimos.service;

import com.wylianedamaso.emprestimos.exception.ConflitoException;
import com.wylianedamaso.emprestimos.exception.NaoEncontradoException;
import com.wylianedamaso.emprestimos.model.Equipamento;
import com.wylianedamaso.emprestimos.model.TipoEquipamento;
import com.wylianedamaso.emprestimos.repository.EmprestimoRepository;
import com.wylianedamaso.emprestimos.repository.EquipamentoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EquipamentoService {

    private final EquipamentoRepository repository;
    private final EmprestimoRepository emprestimoRepository;

    public EquipamentoService(EquipamentoRepository repository, EmprestimoRepository emprestimoRepository){
        this.repository = repository;
        this.emprestimoRepository = emprestimoRepository;
    }

    public List<Equipamento> listar(TipoEquipamento tipo, Boolean disponivel){
         if (tipo != null && disponivel != null) {
             return repository.findByTipoAndDisponivel(tipo,disponivel);
         }
         if (tipo != null) {
             return repository.findByTipo(tipo);
         }
         if (disponivel != null){
             return repository.findByDisponivel(disponivel);
         }
         return repository.findAll();
    }

    public Equipamento buscar(Long id){
        return repository.findById(id)
                .orElseThrow(() -> new NaoEncontradoException("Equipamento não encontrado"));
    }

    public Equipamento cadastrar(Equipamento equipamento){
        if (repository.existsByPatrimonio(equipamento.getPatrimonio())){
            throw new ConflitoException("Patrimônio já cadastrado");
        }
        equipamento.setDisponivel(true);
        return repository.save(equipamento);
    }

    public Equipamento atualizar(Long id, Equipamento dados){
        Equipamento equipamento = buscar(id);
        equipamento.setNome(dados.getNome());
        equipamento.setTipo(dados.getTipo());
        return repository.save(equipamento);
    }

    public void remover(Long id){
        Equipamento equipamento = buscar(id);
        if (emprestimoRepository.existsByEquipamentoId(id)) {
            throw new ConflitoException("Equipamento tem empréstimos registrados");
        }
        repository.delete(equipamento);
    }
}

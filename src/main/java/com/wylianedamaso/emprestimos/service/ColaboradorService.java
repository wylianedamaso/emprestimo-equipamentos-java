package com.wylianedamaso.emprestimos.service;

import com.wylianedamaso.emprestimos.exception.ConflitoException;
import com.wylianedamaso.emprestimos.exception.NaoEncontradoException;
import com.wylianedamaso.emprestimos.model.Colaborador;
import com.wylianedamaso.emprestimos.repository.ColaboradorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ColaboradorService {

    private final ColaboradorRepository repository;

    public ColaboradorService(ColaboradorRepository repository){
        this.repository = repository;
    }

    public List<Colaborador> listar(){
        return repository.findAll();
    }

    public Colaborador buscar(Long id){
        return repository.findById(id).orElseThrow(() -> new NaoEncontradoException("Colaborador não encontrado"));
    }

    public Colaborador cadastrar(Colaborador colaborador){
        if (repository.existsByEmail(colaborador.getEmail())) {
            throw  new ConflitoException("E-mail já cadastrado");
        }
        return repository.save(colaborador);
    }
}

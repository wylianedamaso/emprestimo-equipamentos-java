package com.wylianedamaso.emprestimos.controller;

import com.wylianedamaso.emprestimos.model.Colaborador;
import com.wylianedamaso.emprestimos.model.Emprestimo;
import com.wylianedamaso.emprestimos.service.ColaboradorService;
import com.wylianedamaso.emprestimos.service.EmprestimoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/colaboradores")
public class ColaboradorController {

    private final ColaboradorService service;
    private final EmprestimoService emprestimoService;

    public ColaboradorController(ColaboradorService service, EmprestimoService emprestimoService) {
        this.service = service;
        this.emprestimoService = emprestimoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Colaborador cadastrar(@RequestBody Colaborador colaborador) {
        return service.cadastrar(colaborador);
    }

    @GetMapping
    public List<Colaborador> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public Colaborador buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @GetMapping("/{id}/emprestimos")
    public List<Emprestimo> emprestimos(@PathVariable Long id) {
        return emprestimoService.listarDoColaborador(id);
    }
}
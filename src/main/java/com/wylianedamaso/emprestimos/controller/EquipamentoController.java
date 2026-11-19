package com.wylianedamaso.emprestimos.controller;

import com.wylianedamaso.emprestimos.model.Equipamento;
import com.wylianedamaso.emprestimos.model.TipoEquipamento;
import com.wylianedamaso.emprestimos.service.EquipamentoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/equipamentos")
public class EquipamentoController {

    private final EquipamentoService service;

    public EquipamentoController(EquipamentoService service) {
        this.service = service;
    }

    @GetMapping
    public List<Equipamento> listar(
            @RequestParam(required = false) TipoEquipamento tipo,
            @RequestParam(required = false) Boolean disponivel) {
        return service.listar(tipo, disponivel);
    }

    @GetMapping("/{id}")
    public Equipamento buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Equipamento cadastrar(@RequestBody Equipamento equipamento) {
        return service.cadastrar(equipamento);
    }

    @PutMapping("/{id}")
    public Equipamento atualizar(@PathVariable Long id, @RequestBody Equipamento dados) {
        return service.atualizar(id, dados);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long id) {
        service.remover(id);
    }
}
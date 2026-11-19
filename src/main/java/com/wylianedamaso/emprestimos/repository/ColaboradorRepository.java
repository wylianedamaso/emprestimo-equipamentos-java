package com.wylianedamaso.emprestimos.repository;

import com.wylianedamaso.emprestimos.model.Colaborador;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ColaboradorRepository extends JpaRepository<Colaborador, Long> {
    boolean existsByEmail(String email);
}

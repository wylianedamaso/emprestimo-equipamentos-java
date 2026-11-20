package com.wylianedamaso.emprestimos.controller;

import com.wylianedamaso.emprestimos.dto.LoginRequest;
import com.wylianedamaso.emprestimos.dto.TokenResposta;
import com.wylianedamaso.emprestimos.security.TokenService;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public AuthController(AuthenticationManager authenticationManager, TokenService tokenService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public TokenResposta login(@RequestBody @Valid LoginRequest dados) {
        Authentication autenticacao = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dados.usuario(), dados.senha()));
        String perfil = autenticacao.getAuthorities().iterator().next()
                .getAuthority().replace("ROLE_", "");
        return new TokenResposta(tokenService.gerar(autenticacao.getName(), perfil));
    }
}
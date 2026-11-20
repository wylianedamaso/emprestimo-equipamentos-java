package com.wylianedamaso.emprestimos.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class FiltroJwt extends OncePerRequestFilter {

    private final TokenService tokenService;

    public FiltroJwt(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String cabecalho = request.getHeader("Authorization");
        if (cabecalho != null && cabecalho.startsWith("Bearer ")) {
            String token = cabecalho.substring(7);
            try {
                Claims dados = tokenService.ler(token);
                String perfil = dados.get("perfil", String.class);
                UsernamePasswordAuthenticationToken autenticacao = new UsernamePasswordAuthenticationToken(
                        dados.getSubject(), null, List.of(new SimpleGrantedAuthority("ROLE_" + perfil)));
                SecurityContextHolder.getContext().setAuthentication(autenticacao);
            } catch (JwtException e) {
                // Token inválido ou vencido: segue sem autenticação (as regras devolvem 401)
            }
        }
        chain.doFilter(request, response);
    }
}
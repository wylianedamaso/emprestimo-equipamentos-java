package com.wylianedamaso.emprestimos.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class TokenService {

    private static final long DUAS_HORAS = 2 * 60 * 60 * 1000;
    private final SecretKey chave;

    public TokenService(@Value("${jwt.segredo}") String segredo) {
        this.chave = Keys.hmacShaKeyFor(segredo.getBytes(StandardCharsets.UTF_8));
    }

    public String gerar(String usuario, String perfil) {
        Date agora = new Date();
        return Jwts.builder()
                .subject(usuario)
                .claim("perfil", perfil)
                .issuedAt(agora)
                .expiration(new Date(agora.getTime() + DUAS_HORAS))
                .signWith(chave)
                .compact();
    }

    public Claims ler(String token) {
        return Jwts.parser()
                .verifyWith(chave)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
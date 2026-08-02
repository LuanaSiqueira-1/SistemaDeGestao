package com.concessionaria.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    // CHAVE SECRETA: String longa criptografada em Base64 para assinar os tokens (HS256)
    private static final String SECRET_KEY = "NDBkM2Q5YjRlNTc2ZTA1YjU3YzgxMzhlM2U4Y2E5ZWY3YzRlOGI2ZDllM2Y0YTFlNWM3YjNkMmU1ZTkxM2M0Yg==";

    // 1. Extrai o e-mail (username) do token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // 2. Extrai qualquer informação (Claim) específica do token
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // 3. Gera um token simples contendo apenas os dados padrão do UserDetails
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    // 4. Mapeia informações extras (como a Role/Função) e gera o token completo
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts
                .builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername()) // No Spring Security, o username será o e-mail
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // Expira em 24 horas
                .signWith(getSignInKey(), Jwts.SIG.HS256)
                .compact();
    }

    // 5. Verifica se o token pertence àquele usuário e se não está expirado
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // 6. Decodifica e lê o conteúdo do token usando a chave secreta
    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // 7. Transforma a String Base64 em uma chave criptográfica real
    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
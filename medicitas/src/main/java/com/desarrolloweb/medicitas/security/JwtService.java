package com.desarrolloweb.medicitas.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

@Service
public class JwtService {

    private final SecretKey clave;
    private final long expiracionMs;

    public JwtService(
            @Value("${app.jwt.secret}") String secretBase64,
            @Value("${app.jwt.expiration-ms}") long expiracionMs) {
        this.clave = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretBase64));
        this.expiracionMs = expiracionMs;
    }

    public String generarToken(UserDetails usuario) {

        Date ahora = new Date();

        List<String> roles = usuario.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .toList();

        return Jwts.builder()
                .subject(usuario.getUsername())
                .claim("roles", roles)
                .issuedAt(ahora)
                .expiration(new Date(ahora.getTime() + expiracionMs))
                .signWith(clave)
                .compact();
    }

    /** Lanza JwtException si el token esta mal formado, alterado o vencido. */
    public String extraerUsername(String token) {
        return extraerClaims(token).getSubject();
    }

    /** true solo si la firma es correcta, no ha vencido y pertenece al usuario. */
    public boolean esTokenValido(String token, UserDetails usuario) {
        try {
            String username = extraerUsername(token);
            return username.equals(usuario.getUsername());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims extraerClaims(String token) {
        return Jwts.parser()
                .verifyWith(clave)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}

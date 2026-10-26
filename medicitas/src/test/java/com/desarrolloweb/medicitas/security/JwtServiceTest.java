package com.desarrolloweb.medicitas.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Encoders;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Pruebas unitarias de tokens: validos, invalidos, expirados, de otro usuario. */
class JwtServiceTest {

    private String secret;
    private JwtService jwtService;
    private UsuarioPrincipal admin;

    @BeforeEach
    void setUp() {
        secret = Encoders.BASE64.encode(Jwts.SIG.HS256.key().build().getEncoded());
        jwtService = new JwtService(secret, 3_600_000);
        admin = usuario("admin", "ROLE_ADMIN");
    }

    @Test
    void tokenValido() {
        String token = jwtService.generarToken(admin);

        assertEquals("admin", jwtService.extraerUsername(token));
        assertTrue(jwtService.esTokenValido(token, admin));
    }

    @Test
    void tokenExpirado() {
        JwtService conExpiracionNegativa = new JwtService(secret, -1_000);
        String token = conExpiracionNegativa.generarToken(admin);

        assertFalse(jwtService.esTokenValido(token, admin));
    }

    @Test
    void tokenAlterado() {
        String token = jwtService.generarToken(admin);

        assertFalse(jwtService.esTokenValido(token + "x", admin));
    }

    @Test
    void tokenBasura() {
        assertFalse(jwtService.esTokenValido("esto.no.es.un.jwt", admin));
    }

    @Test
    void tokenFirmadoConOtraClave() {
        String otroSecret = Encoders.BASE64.encode(Jwts.SIG.HS256.key().build().getEncoded());
        String token = new JwtService(otroSecret, 3_600_000).generarToken(admin);

        assertFalse(jwtService.esTokenValido(token, admin));
    }

    @Test
    void tokenDeOtroUsuario() {
        String token = jwtService.generarToken(admin);

        assertFalse(jwtService.esTokenValido(token, usuario("auditor", "ROLE_AUDITOR")));
    }

    private UsuarioPrincipal usuario(String username, String rol) {
        List<GrantedAuthority> roles = List.of(new SimpleGrantedAuthority(rol));
        return new UsuarioPrincipal(1L, username, "x", true, null, roles);
    }
}

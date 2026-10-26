package com.desarrolloweb.medicitas.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.desarrolloweb.medicitas.dto.LoginRequestDTO;
import com.desarrolloweb.medicitas.dto.LoginResponseDTO;
import com.desarrolloweb.medicitas.security.JwtService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto) {

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.username(), dto.password()));

        UserDetails usuario = (UserDetails) auth.getPrincipal();

        String token = jwtService.generarToken(usuario);

        return ResponseEntity.ok(new LoginResponseDTO(
                token,
                "Bearer",
                usuario.getUsername(),
                usuario.getAuthorities().stream().map(a -> a.getAuthority()).toList()));
    }
}

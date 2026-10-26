package com.desarrolloweb.medicitas.security;

import com.desarrolloweb.medicitas.model.Usuario;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class UsuarioPrincipal implements UserDetails {

    private final Long id;
    private final String username;
    private final String password;
    private final boolean activo;
    private final Long medicoId;
    private final List<GrantedAuthority> authorities;

    public UsuarioPrincipal(Long id, String username, String password, boolean activo,
                            Long medicoId, List<GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.activo = activo;
        this.medicoId = medicoId;
        this.authorities = authorities;
    }

    public static UsuarioPrincipal from(Usuario usuario) {

        List<GrantedAuthority> authorities = usuario.getRoles().stream()
                .map(rol -> (GrantedAuthority) new SimpleGrantedAuthority(rol.getNombre().name()))
                .toList();

        Long medicoId = usuario.getMedico() != null ? usuario.getMedico().getId() : null;

        return new UsuarioPrincipal(
                usuario.getId(),
                usuario.getUsername(),
                usuario.getPassword(),
                Boolean.TRUE.equals(usuario.getActivo()),
                medicoId,
                authorities);
    }

    public Long getId() {
        return id;
    }

    public Long getMedicoId() {
        return medicoId;
    }

    public boolean tieneRol(String rol) {
        return authorities.stream().anyMatch(a -> a.getAuthority().equals(rol));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isEnabled() {
        return activo;
    }
}

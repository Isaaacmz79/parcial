package com.desarrolloweb.medicitas.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static Optional<UsuarioPrincipal> usuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UsuarioPrincipal principal) {
            return Optional.of(principal);
        }
        return Optional.empty();
    }

    /** true si el usuario es MEDICO y NO es ADMIN (el admin no tiene restriccion por registro). */
    public static boolean esMedico() {
        return usuarioActual()
                .map(p -> p.tieneRol("ROLE_MEDICO") && !p.tieneRol("ROLE_ADMIN"))
                .orElse(false);
    }

    /** ID del medico asociado al usuario autenticado. */
    public static Long medicoIdActual() {
        return usuarioActual()
                .map(UsuarioPrincipal::getMedicoId)
                .orElseThrow(() -> new AccessDeniedException("Su usuario no está asociado a un médico"));
    }
}

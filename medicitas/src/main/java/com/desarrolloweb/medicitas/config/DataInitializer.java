package com.desarrolloweb.medicitas.config;

import com.desarrolloweb.medicitas.model.Medico;
import com.desarrolloweb.medicitas.model.NombreRol;
import com.desarrolloweb.medicitas.model.Rol;
import com.desarrolloweb.medicitas.model.Usuario;
import com.desarrolloweb.medicitas.repository.MedicoRepository;
import com.desarrolloweb.medicitas.repository.RolRepository;
import com.desarrolloweb.medicitas.repository.UsuarioRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Carga inicial: roles y usuarios de prueba (solo si no existen).
 *
 *  admin      / admin123   -> ROLE_ADMIN
 *  recepcion  / recep123   -> ROLE_RECEPCIONISTA
 *  auditor    / audit123   -> ROLE_AUDITOR
 *  medico1    / medico123  -> ROLE_MEDICO (Medicina General)
 *  medico2    / medico123  -> ROLE_MEDICO (Cardiologia)
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;
    private final MedicoRepository medicoRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(RolRepository rolRepository,
                           UsuarioRepository usuarioRepository,
                           MedicoRepository medicoRepository,
                           PasswordEncoder passwordEncoder) {
        this.rolRepository = rolRepository;
        this.usuarioRepository = usuarioRepository;
        this.medicoRepository = medicoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {

        for (NombreRol nombre : NombreRol.values()) {
            if (rolRepository.findByNombre(nombre).isEmpty()) {
                rolRepository.save(new Rol(nombre));
            }
        }

        crearUsuario("admin", "admin123", NombreRol.ROLE_ADMIN, null);
        crearUsuario("recepcion", "recep123", NombreRol.ROLE_RECEPCIONISTA, null);
        crearUsuario("auditor", "audit123", NombreRol.ROLE_AUDITOR, null);

        Medico m1 = obtenerOCrearMedico("Juan José Gómez", "Medicina General", "medico1@medicitas.com");
        Medico m2 = obtenerOCrearMedico("Ana María Torres", "Cardiología", "medico2@medicitas.com");

        crearUsuario("medico1", "medico123", NombreRol.ROLE_MEDICO, m1);
        crearUsuario("medico2", "medico123", NombreRol.ROLE_MEDICO, m2);
    }

    private Medico obtenerOCrearMedico(String nombre, String especialidad, String correo) {

        return medicoRepository.findByCorreo(correo).orElseGet(() -> {
            Medico m = new Medico();
            m.setNombre(nombre);
            m.setEspecialidad(especialidad);
            m.setCorreo(correo);
            m.setActivo(true);
            return medicoRepository.save(m);
        });
    }

    private void crearUsuario(String username, String password, NombreRol rol, Medico medico) {

        if (usuarioRepository.existsByUsername(username)) {
            return;
        }

        Usuario u = new Usuario();
        u.setUsername(username);
        u.setPassword(passwordEncoder.encode(password));
        u.setActivo(true);
        u.setMedico(medico);
        u.getRoles().add(rolRepository.findByNombre(rol).orElseThrow());

        usuarioRepository.save(u);
    }
}

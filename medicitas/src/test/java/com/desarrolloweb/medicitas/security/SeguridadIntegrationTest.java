package com.desarrolloweb.medicitas.security;

import com.desarrolloweb.medicitas.model.CitaMedica;
import com.desarrolloweb.medicitas.model.EstadoCita;
import com.desarrolloweb.medicitas.model.Paciente;
import com.desarrolloweb.medicitas.repository.CitaMedicaRepository;
import com.desarrolloweb.medicitas.repository.PacienteRepository;
import com.desarrolloweb.medicitas.repository.UsuarioRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
// Spring Boot 4: si tu IDE no resuelve este import, usa
// org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Requiere MySQL activo (igual que contextLoads). Los usuarios de prueba los
 * crea DataInitializer al arrancar el contexto.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SeguridadIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired JwtService jwtService;
    @Autowired CustomUserDetailsService userDetailsService;
    @Autowired UsuarioRepository usuarioRepository;
    @Autowired PacienteRepository pacienteRepository;
    @Autowired CitaMedicaRepository citaMedicaRepository;

    @Value("${app.jwt.secret}") String secret;

    private String token(String username) {
        return "Bearer " + jwtService.generarToken(userDetailsService.loadUserByUsername(username));
    }

    // ---------- 401 ----------

    @Test
    void sinToken_401() throws Exception {
        mockMvc.perform(get("/api/citas")).andExpect(status().isUnauthorized());
    }

    @Test
    void tokenInvalido_401() throws Exception {
        mockMvc.perform(get("/api/citas").header("Authorization", "Bearer abc.def.ghi"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void tokenExpirado_401() throws Exception {
        JwtService vencido = new JwtService(secret, -1_000);
        String t = vencido.generarToken(userDetailsService.loadUserByUsername("admin"));

        mockMvc.perform(get("/api/citas").header("Authorization", "Bearer " + t))
                .andExpect(status().isUnauthorized());
    }

    // ---------- login ----------

    @Test
    void login_ok_200() throws Exception {
        mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"password\":\"admin123\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void login_credencialesIncorrectas_401() throws Exception {
        mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"password\":\"mala\"}"))
                .andExpect(status().isUnauthorized());
    }

    // ---------- roles (403 / 200) ----------

    @Test
    void admin_puedeListarCitasYVerResumen() throws Exception {
        mockMvc.perform(get("/api/citas").header("Authorization", token("admin")))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/citas/resumen-medicos").header("Authorization", token("admin")))
                .andExpect(status().isOk());
    }

    @Test
    void auditor_puedeVerResumen_peroNoCrearCitas() throws Exception {
        mockMvc.perform(get("/api/citas/resumen-medicos").header("Authorization", token("auditor")))
                .andExpect(status().isOk());
        mockMvc.perform(post("/api/citas").header("Authorization", token("auditor"))
                        .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void recepcionista_noVeResumen_403() throws Exception {
        mockMvc.perform(get("/api/citas/resumen-medicos").header("Authorization", token("recepcion")))
                .andExpect(status().isForbidden());
    }

    @Test
    void medico_noPuedeCrearMedicos_403() throws Exception {
        mockMvc.perform(post("/api/medicos").header("Authorization", token("medico1"))
                        .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void medico_noPuedeVerResumen_403() throws Exception {
        mockMvc.perform(get("/api/citas/resumen-medicos").header("Authorization", token("medico1")))
                .andExpect(status().isForbidden());
    }

    // ---------- autorizacion por registro ----------

    @Test
    void medico_noPuedeVerNiCambiarCitaDeOtroMedico() throws Exception {
        Long citaDeMedico2 = crearCitaDeMedico2();

        // medico1 manipulando el ID en la URL
        mockMvc.perform(get("/api/citas/" + citaDeMedico2).header("Authorization", token("medico1")))
                .andExpect(status().isForbidden());

        mockMvc.perform(patch("/api/citas/" + citaDeMedico2 + "/estado")
                        .header("Authorization", token("medico1"))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"estado\":\"CANCELADA\"}"))
                .andExpect(status().isForbidden());

        // medico2 (dueño) si puede
        mockMvc.perform(get("/api/citas/" + citaDeMedico2).header("Authorization", token("medico2")))
                .andExpect(status().isOk());
    }

    @Test
    void medico_noPuedeFiltrarPorMedicoIdAjeno() throws Exception {
        Long medico2Id = usuarioRepository.findByUsername("medico2").orElseThrow().getMedico().getId();

        mockMvc.perform(get("/api/citas?medicoId=" + medico2Id).header("Authorization", token("medico1")))
                .andExpect(status().isForbidden());
    }

    @Test
    void medico_noPuedeVerPacienteSinRelacion() throws Exception {
        Long citaDeMedico2 = crearCitaDeMedico2();
        Long pacienteId = citaMedicaRepository.findById(citaDeMedico2).orElseThrow().getPaciente().getId();

        mockMvc.perform(get("/api/pacientes/" + pacienteId).header("Authorization", token("medico1")))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/api/pacientes/" + pacienteId).header("Authorization", token("medico2")))
                .andExpect(status().isOk());
    }

    private Long crearCitaDeMedico2() {
        Paciente p = new Paciente();
        p.setNombre("Paciente Prueba");
        p.setDocumento(UUID.randomUUID().toString().substring(0, 12));
        p.setCorreo(UUID.randomUUID().toString().substring(0, 8) + "@test.com");
        p.setTelefono("3000000000");
        p = pacienteRepository.save(p);

        CitaMedica c = new CitaMedica();
        c.setFechaHora(LocalDateTime.now().plusDays(5));
        c.setMotivo("Control");
        c.setEstado(EstadoCita.PROGRAMADA);
        c.setPaciente(p);
        c.setMedico(usuarioRepository.findByUsername("medico2").orElseThrow().getMedico());

        return citaMedicaRepository.save(c).getId();
    }
}


package com.desarrolloweb.medicitas.controller;

import com.desarrolloweb.medicitas.dto.PacienteRequestDTO;
import com.desarrolloweb.medicitas.dto.PacienteResponseDTO;
import com.desarrolloweb.medicitas.service.PacienteService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/pacientes")
public class PacienteController {

    private final PacienteService pacienteService;

    public PacienteController(PacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }

    @PostMapping
    public ResponseEntity<PacienteResponseDTO> crear(
            @Valid @RequestBody PacienteRequestDTO dto) {

        PacienteResponseDTO respuesta = pacienteService.crear(dto);

        URI ubicacion = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(respuesta.id())
                .toUri();

        return ResponseEntity.created(ubicacion).body(respuesta);
    }

    @GetMapping
    public ResponseEntity<List<PacienteResponseDTO>> listar() {
        return ResponseEntity.ok(pacienteService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PacienteResponseDTO> buscarPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(pacienteService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PacienteResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody PacienteRequestDTO dto) {

        return ResponseEntity.ok(
                pacienteService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {

        pacienteService.eliminar(id);

        return ResponseEntity.noContent().build();
    }
}

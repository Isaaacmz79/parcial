package com.desarrolloweb.medicitas.controller;

import com.desarrolloweb.medicitas.dto.CambioEstadoCitaDTO;
import com.desarrolloweb.medicitas.dto.CitaMedicaRequestDTO;
import com.desarrolloweb.medicitas.dto.CitaMedicaResponseDTO;
import com.desarrolloweb.medicitas.service.CitaMedicaService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/citas")
public class CitaMedicaController {

    private final CitaMedicaService citaMedicaService;

    public CitaMedicaController(CitaMedicaService citaMedicaService) {
        this.citaMedicaService = citaMedicaService;
    }

    @PostMapping
    public ResponseEntity<CitaMedicaResponseDTO> crear(
            @Valid @RequestBody CitaMedicaRequestDTO dto) {

        CitaMedicaResponseDTO respuesta = citaMedicaService.crear(dto);

        URI ubicacion = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(respuesta.id())
                .toUri();

        return ResponseEntity.created(ubicacion).body(respuesta);
    }

    @GetMapping
    public ResponseEntity<List<CitaMedicaResponseDTO>> listar() {
        return ResponseEntity.ok(citaMedicaService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CitaMedicaResponseDTO> buscarPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(citaMedicaService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CitaMedicaResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody CitaMedicaRequestDTO dto) {

        return ResponseEntity.ok(
                citaMedicaService.actualizar(id, dto));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<CitaMedicaResponseDTO> cambiarEstado(
            @PathVariable Long id,
            @Valid @RequestBody CambioEstadoCitaDTO dto) {

        return ResponseEntity.ok(
                citaMedicaService.cambiarEstado(id, dto.estado()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {

        citaMedicaService.eliminar(id);

        return ResponseEntity.noContent().build();
    }
}

package com.desarrolloweb.medicitas.controller;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.desarrolloweb.medicitas.dto.CambioEstadoCitaDTO;
import com.desarrolloweb.medicitas.dto.CitaMedicaFiltroDTO;
import com.desarrolloweb.medicitas.dto.CitaMedicaRequestDTO;
import com.desarrolloweb.medicitas.dto.CitaMedicaResponseDTO;
import com.desarrolloweb.medicitas.dto.MedicoResumenDTO;
import com.desarrolloweb.medicitas.model.EstadoCita;
import com.desarrolloweb.medicitas.service.CitaMedicaService;

import jakarta.validation.Valid;

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
    public ResponseEntity<List<CitaMedicaResponseDTO>> listar(
            @RequestParam(required = false) EstadoCita estado,
            @RequestParam(required = false) Long medicoId,
            @RequestParam(required = false) Long pacienteId,
            @RequestParam(required = false) String especialidad,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaDesde,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaHasta,
            @RequestParam(required = false) String nombrePaciente) {

        CitaMedicaFiltroDTO filtro = new CitaMedicaFiltroDTO(
                estado, medicoId, pacienteId, especialidad,
                fechaDesde, fechaHasta, nombrePaciente);

        return ResponseEntity.ok(citaMedicaService.listar(filtro));
    }

    @GetMapping("/resumen-medicos")
    public ResponseEntity<List<MedicoResumenDTO>> resumenMedicos(
            @RequestParam(required = false) String especialidad,
            @RequestParam(required = false) EstadoCita estado,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaDesde,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaHasta) {

        return ResponseEntity.ok(
                citaMedicaService.resumenPorMedico(especialidad, estado, fechaDesde, fechaHasta));
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

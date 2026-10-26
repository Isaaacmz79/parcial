package com.desarrolloweb.medicitas.dto;

import com.desarrolloweb.medicitas.model.EstadoCita;

import java.time.LocalDateTime;

public record CitaMedicaFiltroDTO(
    EstadoCita estado,
    Long medicoId,
    Long pacienteId,
    String especialidad,
    LocalDateTime fechaDesde,
    LocalDateTime fechaHasta,
    String nombrePaciente
) {}

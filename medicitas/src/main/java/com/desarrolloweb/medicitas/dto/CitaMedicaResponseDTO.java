package com.desarrolloweb.medicitas.dto;

import com.desarrolloweb.medicitas.model.EstadoCita;
import java.time.LocalDateTime;

public record CitaMedicaResponseDTO(
    Long id,
    LocalDateTime fechaHora,
    EstadoCita estado,
    Long pacienteId,
    String pacienteNombre,
    Long medicoId,
    String medicoNombre,
    String especialidad
) {}
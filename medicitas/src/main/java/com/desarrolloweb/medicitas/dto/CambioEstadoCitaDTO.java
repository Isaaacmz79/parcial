package com.desarrolloweb.medicitas.dto;

import com.desarrolloweb.medicitas.model.EstadoCita;
import jakarta.validation.constraints.NotNull;

public record CambioEstadoCitaDTO(
        @NotNull(message = "El nuevo estado es obligatorio")
        EstadoCita estado
) {}

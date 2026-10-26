package com.desarrolloweb.medicitas.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CitaMedicaRequestDTO(

    @NotNull(message = "La fecha y hora son obligatorias")
    @Future(message = "La cita debe programarse en el futuro")
    LocalDateTime fechaHora,

    @NotBlank(message = "El motivo es obligatorio")
    String motivo,

    @NotNull(message = "Debe seleccionarse un paciente")
    Long pacienteId,

    @NotNull(message = "Debe seleccionarse un médico")
    Long medicoId

) {}
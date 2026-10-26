package com.desarrolloweb.medicitas.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MedicoRequestDTO(

    @NotBlank(message = "El nombre es obligatorio")
    String nombre,

    @NotBlank(message = "La especialidad es obligatoria")
    String especialidad,

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo no tiene un formato válido")
    String correo,

    @NotNull(message = "Debe indicarse si el médico está activo")
    Boolean activo

) {}
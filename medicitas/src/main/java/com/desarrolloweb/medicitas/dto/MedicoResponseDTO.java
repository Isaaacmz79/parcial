package com.desarrolloweb.medicitas.dto;

public record MedicoResponseDTO(
    Long id,
    String nombre,
    String especialidad,
    Boolean activo
) {}
package com.desarrolloweb.medicitas.dto;

public record MedicoResumenDTO(
    Long medicoId,
    String nombre,
    String especialidad,
    Long totalCitas,
    Long citasAtendidas,
    Long citasCanceladas
) {}

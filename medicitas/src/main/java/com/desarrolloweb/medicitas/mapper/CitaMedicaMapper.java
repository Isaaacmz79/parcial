package com.desarrolloweb.medicitas.mapper;

import com.desarrolloweb.medicitas.dto.CitaMedicaRequestDTO;
import com.desarrolloweb.medicitas.dto.CitaMedicaResponseDTO;
import com.desarrolloweb.medicitas.model.CitaMedica;
import com.desarrolloweb.medicitas.model.EstadoCita;

public class CitaMedicaMapper {

    private CitaMedicaMapper() {
    }

    public static CitaMedica toEntity(CitaMedicaRequestDTO dto) {

        CitaMedica cita = new CitaMedica();

        cita.setFechaHora(dto.fechaHora());
        cita.setMotivo(dto.motivo());
        cita.setEstado(EstadoCita.PROGRAMADA);

        return cita;
    }

    public static CitaMedicaResponseDTO toResponseDTO(CitaMedica cita) {

        return new CitaMedicaResponseDTO(
                cita.getId(),
                cita.getFechaHora(),
                cita.getEstado(),
                cita.getPaciente().getId(),
                cita.getPaciente().getNombre(),
                cita.getMedico().getId(),
                cita.getMedico().getNombre(),
                cita.getMedico().getEspecialidad()
        );
    }
}
package com.desarrolloweb.medicitas.mapper;

import com.desarrolloweb.medicitas.dto.PacienteRequestDTO;
import com.desarrolloweb.medicitas.dto.PacienteResponseDTO;
import com.desarrolloweb.medicitas.model.Paciente;

public class PacienteMapper {

    private PacienteMapper() {
    }

    public static Paciente toEntity(PacienteRequestDTO dto) {

        Paciente paciente = new Paciente();

        paciente.setNombre(dto.nombre());
        paciente.setDocumento(dto.documento());
        paciente.setCorreo(dto.correo());
        paciente.setTelefono(dto.telefono());

        return paciente;
    }

    public static PacienteResponseDTO toResponseDTO(Paciente paciente) {

        return new PacienteResponseDTO(
                paciente.getId(),
                paciente.getNombre()
        );
    }
}
package com.desarrolloweb.medicitas.mapper;

import com.desarrolloweb.medicitas.dto.MedicoRequestDTO;
import com.desarrolloweb.medicitas.dto.MedicoResponseDTO;
import com.desarrolloweb.medicitas.model.Medico;

public class MedicoMapper {

    private MedicoMapper() {
    }

    public static Medico toEntity(MedicoRequestDTO dto) {

        Medico medico = new Medico();

        medico.setNombre(dto.nombre());
        medico.setEspecialidad(dto.especialidad());
        medico.setCorreo(dto.correo());
        medico.setActivo(dto.activo());

        return medico;
    }

    public static MedicoResponseDTO toResponseDTO(Medico medico) {

        return new MedicoResponseDTO(
                medico.getId(),
                medico.getNombre(),
                medico.getEspecialidad(),
                medico.getActivo()
        );
    }
}
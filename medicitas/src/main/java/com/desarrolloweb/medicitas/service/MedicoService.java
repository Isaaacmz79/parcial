package com.desarrolloweb.medicitas.service;

import com.desarrolloweb.medicitas.dto.MedicoRequestDTO;
import com.desarrolloweb.medicitas.dto.MedicoResponseDTO;

import java.util.List;

public interface MedicoService {

    MedicoResponseDTO crear(MedicoRequestDTO dto);

    List<MedicoResponseDTO> listar();

    MedicoResponseDTO buscarPorId(Long id);

    MedicoResponseDTO actualizar(Long id, MedicoRequestDTO dto);

    void eliminar(Long id);
}
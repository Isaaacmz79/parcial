package com.desarrolloweb.medicitas.service;

import com.desarrolloweb.medicitas.dto.PacienteRequestDTO;
import com.desarrolloweb.medicitas.dto.PacienteResponseDTO;

import java.util.List;

public interface PacienteService {

    PacienteResponseDTO crear(PacienteRequestDTO dto);

    List<PacienteResponseDTO> listar();

    PacienteResponseDTO buscarPorId(Long id);

    PacienteResponseDTO actualizar(Long id, PacienteRequestDTO dto);

    void eliminar(Long id);
}
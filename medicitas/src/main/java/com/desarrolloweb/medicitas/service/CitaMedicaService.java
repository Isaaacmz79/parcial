package com.desarrolloweb.medicitas.service;

import com.desarrolloweb.medicitas.dto.CitaMedicaRequestDTO;
import com.desarrolloweb.medicitas.dto.CitaMedicaResponseDTO;
import com.desarrolloweb.medicitas.model.EstadoCita;

import java.util.List;

public interface CitaMedicaService {

    CitaMedicaResponseDTO crear(CitaMedicaRequestDTO dto);

    List<CitaMedicaResponseDTO> listar();

    CitaMedicaResponseDTO buscarPorId(Long id);

    CitaMedicaResponseDTO actualizar(Long id, CitaMedicaRequestDTO dto);

    CitaMedicaResponseDTO cambiarEstado(Long id, EstadoCita nuevoEstado);

    void eliminar(Long id);
}
package com.desarrolloweb.medicitas.service;

import com.desarrolloweb.medicitas.dto.PacienteRequestDTO;
import com.desarrolloweb.medicitas.dto.PacienteResponseDTO;
import com.desarrolloweb.medicitas.exception.BusinessException;
import com.desarrolloweb.medicitas.exception.ResourceNotFoundException;
import com.desarrolloweb.medicitas.mapper.PacienteMapper;
import com.desarrolloweb.medicitas.model.Paciente;
import com.desarrolloweb.medicitas.repository.CitaMedicaRepository;
import com.desarrolloweb.medicitas.repository.PacienteRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PacienteServiceImpl implements PacienteService {

    private final PacienteRepository pacienteRepository;
    private final CitaMedicaRepository citaMedicaRepository;

    public PacienteServiceImpl(
            PacienteRepository pacienteRepository,
            CitaMedicaRepository citaMedicaRepository) {
        this.pacienteRepository = pacienteRepository;
        this.citaMedicaRepository = citaMedicaRepository;
    }

    @Override
    public PacienteResponseDTO crear(PacienteRequestDTO dto) {

        if (pacienteRepository.existsByDocumento(dto.documento())) {
            throw new BusinessException(
                    "Ya existe un paciente con ese documento");
        }

        if (pacienteRepository.existsByCorreo(dto.correo())) {
            throw new BusinessException(
                    "Ya existe un paciente con ese correo");
        }

        Paciente paciente = PacienteMapper.toEntity(dto);

        Paciente guardado = pacienteRepository.save(paciente);

        return PacienteMapper.toResponseDTO(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PacienteResponseDTO> listar() {

        return pacienteRepository.findAll()
                .stream()
                .map(PacienteMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PacienteResponseDTO buscarPorId(Long id) {

        Paciente paciente = obtenerPaciente(id);

        return PacienteMapper.toResponseDTO(paciente);
    }

    @Override
    public PacienteResponseDTO actualizar(
            Long id,
            PacienteRequestDTO dto) {

        Paciente paciente = obtenerPaciente(id);

        if (pacienteRepository.existsByDocumentoAndIdNot(
                dto.documento(), id)) {
            throw new BusinessException(
                    "El documento ya pertenece a otro paciente");
        }

        if (pacienteRepository.existsByCorreoAndIdNot(
                dto.correo(), id)) {
            throw new BusinessException(
                    "El correo ya pertenece a otro paciente");
        }

        paciente.setNombre(dto.nombre());
        paciente.setDocumento(dto.documento());
        paciente.setCorreo(dto.correo());
        paciente.setTelefono(dto.telefono());

        Paciente actualizado = pacienteRepository.save(paciente);

        return PacienteMapper.toResponseDTO(actualizado);
    }

    @Override
    public void eliminar(Long id) {

        Paciente paciente = obtenerPaciente(id);

        if (citaMedicaRepository.existsByPaciente_Id(id)) {
            throw new BusinessException(
                    "No se puede eliminar un paciente que tiene citas registradas");
        }

        pacienteRepository.delete(paciente);
    }

    private Paciente obtenerPaciente(Long id) {

        return pacienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No existe un paciente con ID " + id));
    }
}
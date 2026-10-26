package com.desarrolloweb.medicitas.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.desarrolloweb.medicitas.dto.MedicoRequestDTO;
import com.desarrolloweb.medicitas.dto.MedicoResponseDTO;
import com.desarrolloweb.medicitas.exception.BusinessException;
import com.desarrolloweb.medicitas.exception.ResourceNotFoundException;
import com.desarrolloweb.medicitas.mapper.MedicoMapper;
import com.desarrolloweb.medicitas.model.EstadoCita;
import com.desarrolloweb.medicitas.model.Medico;
import com.desarrolloweb.medicitas.repository.CitaMedicaRepository;
import com.desarrolloweb.medicitas.repository.MedicoRepository;
import com.desarrolloweb.medicitas.security.SecurityUtils;

@Service
@Transactional
public class MedicoServiceImpl implements MedicoService {

    private final MedicoRepository medicoRepository;
    private final CitaMedicaRepository citaMedicaRepository;

    public MedicoServiceImpl(
            MedicoRepository medicoRepository,
            CitaMedicaRepository citaMedicaRepository) {
        this.medicoRepository = medicoRepository;
        this.citaMedicaRepository = citaMedicaRepository;
    }

    @Override
    public MedicoResponseDTO crear(MedicoRequestDTO dto) {

        if (medicoRepository.existsByCorreo(dto.correo())) {
            throw new BusinessException(
                    "Ya existe un médico con ese correo");
        }

        Medico medico = MedicoMapper.toEntity(dto);

        Medico guardado = medicoRepository.save(medico);

        return MedicoMapper.toResponseDTO(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicoResponseDTO> listar() {

        return medicoRepository.findAll()
                .stream()
                .map(MedicoMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public MedicoResponseDTO buscarPorId(Long id) {
        
        if (SecurityUtils.esMedico() && !id.equals(SecurityUtils.medicoIdActual())) {
            throw new AccessDeniedException("Un médico solo puede consultar su propio registro");
        }

        Medico medico = obtenerMedico(id);

        return MedicoMapper.toResponseDTO(medico);
    }

    @Override
    public MedicoResponseDTO actualizar(
            Long id,
            MedicoRequestDTO dto) {

        Medico medico = obtenerMedico(id);

        if (medicoRepository.existsByCorreoAndIdNot(
                dto.correo(), id)) {
            throw new BusinessException("El correo ya pertenece a otro médico");
        }

        if (Boolean.TRUE.equals(medico.getActivo())
                && Boolean.FALSE.equals(dto.activo())) {

            boolean tieneCitasPendientes = citaMedicaRepository
                    .existsByMedico_IdAndFechaHoraAfterAndEstado(id, LocalDateTime.now(), EstadoCita.PROGRAMADA);

            if (tieneCitasPendientes) {
                throw new BusinessException("No se puede desactivar un médico con citas futuras programadas");
            }
        }

        medico.setNombre(dto.nombre());
        medico.setEspecialidad(dto.especialidad());
        medico.setCorreo(dto.correo());
        medico.setActivo(dto.activo());

        Medico actualizado = medicoRepository.save(medico);

        return MedicoMapper.toResponseDTO(actualizado);
    }

    @Override
    public void eliminar(Long id) {

        Medico medico = obtenerMedico(id);

        if (citaMedicaRepository.existsByMedico_Id(id)) {
            throw new BusinessException("No se puede eliminar un médico que tiene citas registradas");
        }

        medicoRepository.delete(medico);
    }

    private Medico obtenerMedico(Long id) {

        return medicoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe un médico con ID " + id));
    }
}

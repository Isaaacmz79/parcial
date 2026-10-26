
package com.desarrolloweb.medicitas.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.desarrolloweb.medicitas.dto.CitaMedicaFiltroDTO;
import com.desarrolloweb.medicitas.dto.CitaMedicaRequestDTO;
import com.desarrolloweb.medicitas.dto.CitaMedicaResponseDTO;
import com.desarrolloweb.medicitas.dto.MedicoResumenDTO;
import com.desarrolloweb.medicitas.exception.BusinessException;
import com.desarrolloweb.medicitas.exception.ResourceNotFoundException;
import com.desarrolloweb.medicitas.mapper.CitaMedicaMapper;
import com.desarrolloweb.medicitas.model.CitaMedica;
import com.desarrolloweb.medicitas.model.EstadoCita;
import com.desarrolloweb.medicitas.model.Medico;
import com.desarrolloweb.medicitas.model.Paciente;
import com.desarrolloweb.medicitas.repository.CitaMedicaRepository;
import com.desarrolloweb.medicitas.repository.MedicoRepository;
import com.desarrolloweb.medicitas.repository.PacienteRepository;
import com.desarrolloweb.medicitas.security.SecurityUtils;
import com.desarrolloweb.medicitas.specification.CitaMedicaSpecifications;

@Service
@Transactional
public class CitaMedicaServiceImpl implements CitaMedicaService {

    private final CitaMedicaRepository citaMedicaRepository;
    private final PacienteRepository pacienteRepository;
    private final MedicoRepository medicoRepository;

    public CitaMedicaServiceImpl(
            CitaMedicaRepository citaMedicaRepository,
            PacienteRepository pacienteRepository,
            MedicoRepository medicoRepository) {
        this.citaMedicaRepository = citaMedicaRepository;
        this.pacienteRepository = pacienteRepository;
        this.medicoRepository = medicoRepository;
    }

    @Override
    public CitaMedicaResponseDTO crear(CitaMedicaRequestDTO dto) {

        validarFechaFutura(dto.fechaHora());

        Paciente paciente = obtenerPaciente(dto.pacienteId());
        Medico medico = obtenerMedico(dto.medicoId());

        validarMedicoActivo(medico);

        CitaMedica cita = CitaMedicaMapper.toEntity(dto);
        cita.setPaciente(paciente);
        cita.setMedico(medico);

        CitaMedica guardada = citaMedicaRepository.save(cita);

        return CitaMedicaMapper.toResponseDTO(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CitaMedicaResponseDTO> listar(CitaMedicaFiltroDTO filtro) {

        validarRangoFechas(filtro.fechaDesde(), filtro.fechaHasta());

        CitaMedicaFiltroDTO filtroAplicado = filtro;

        if (SecurityUtils.esMedico()) {
            Long propio = SecurityUtils.medicoIdActual();

            if (filtro.medicoId() != null && !filtro.medicoId().equals(propio)) {
                throw new AccessDeniedException("Un médico solo puede consultar sus propias citas");
            }

            filtroAplicado = new CitaMedicaFiltroDTO(
                    filtro.estado(), propio, filtro.pacienteId(), filtro.especialidad(),
                    filtro.fechaDesde(), filtro.fechaHasta(), filtro.nombrePaciente());
        }

        return citaMedicaRepository
                .findAll(CitaMedicaSpecifications.conFiltros(filtroAplicado), Sort.by("fechaHora"))
                .stream()
                .map(CitaMedicaMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicoResumenDTO> resumenPorMedico(
            String especialidad,
            EstadoCita estado,
            LocalDateTime fechaDesde,
            LocalDateTime fechaHasta) {

        validarRangoFechas(fechaDesde, fechaHasta);

        String esp = StringUtils.hasText(especialidad) ? especialidad.trim() : null;

        return medicoRepository.obtenerResumenCitasPorMedico(esp, estado, fechaDesde, fechaHasta);
    }

    @Override
    @Transactional(readOnly = true)
    public CitaMedicaResponseDTO buscarPorId(Long id) {

        CitaMedica cita = obtenerCita(id);

        validarAccesoMedico(cita);

        return CitaMedicaMapper.toResponseDTO(cita);
    }

    @Override
    public CitaMedicaResponseDTO actualizar(Long id, CitaMedicaRequestDTO dto) {

        CitaMedica cita = obtenerCita(id);

        if (cita.getEstado() != EstadoCita.PROGRAMADA) {
            throw new BusinessException("Solo se pueden modificar citas programadas");
        }

        validarFechaFutura(dto.fechaHora());

        Paciente paciente = obtenerPaciente(dto.pacienteId());
        Medico medico = obtenerMedico(dto.medicoId());

        validarMedicoActivo(medico);

        cita.setFechaHora(dto.fechaHora());
        cita.setMotivo(dto.motivo());
        cita.setPaciente(paciente);
        cita.setMedico(medico);

        CitaMedica actualizada = citaMedicaRepository.save(cita);

        return CitaMedicaMapper.toResponseDTO(actualizada);
    }

    @Override
    public CitaMedicaResponseDTO cambiarEstado(
            Long id,
            EstadoCita nuevoEstado) {

        CitaMedica cita = obtenerCita(id);

        validarAccesoMedico(cita);

        if (nuevoEstado == null) {
            throw new BusinessException("El nuevo estado es obligatorio");
        }

        if (cita.getEstado() != EstadoCita.PROGRAMADA) {
            throw new BusinessException("Solo se puede cambiar el estado de una cita programada");
        }

        if (nuevoEstado == EstadoCita.PROGRAMADA) {
            throw new BusinessException("La cita ya está programada");
        }

        if (nuevoEstado != EstadoCita.ATENDIDA && nuevoEstado != EstadoCita.CANCELADA) {
            throw new BusinessException("El estado solicitado no está permitido");
        }

        cita.setEstado(nuevoEstado);

        CitaMedica actualizada = citaMedicaRepository.save(cita);

        return CitaMedicaMapper.toResponseDTO(actualizada);
    }

    @Override
    public void eliminar(Long id) {

        CitaMedica cita = obtenerCita(id);

        if (cita.getEstado() == EstadoCita.ATENDIDA) {
            throw new BusinessException("No se puede eliminar una cita que ya fue atendida");
        }

        citaMedicaRepository.delete(cita);
    }

    private CitaMedica obtenerCita(Long id) {

        return citaMedicaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe una cita con ID " + id));
    }

    private Paciente obtenerPaciente(Long id) {

        return pacienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe un paciente con ID " + id));
    }

    private Medico obtenerMedico(Long id) {

        return medicoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe un médico con ID " + id));
    }

    private void validarMedicoActivo(Medico medico) {

        if (!Boolean.TRUE.equals(medico.getActivo())) {
            throw new BusinessException("No se puede programar una cita con un médico inactivo");
        }
    }

    private void validarFechaFutura(LocalDateTime fechaHora) {

        if (fechaHora == null || !fechaHora.isAfter(LocalDateTime.now())) {
            throw new BusinessException("La fecha de la cita debe ser futura");
        }
    }

    private void validarRangoFechas(LocalDateTime desde, LocalDateTime hasta) {

        if (desde != null && hasta != null && desde.isAfter(hasta)) {
            throw new BusinessException("La fecha inicial no puede ser posterior a la fecha final");
        }
    }

    private void validarAccesoMedico(CitaMedica cita) {

        if (SecurityUtils.esMedico()
                && !cita.getMedico().getId().equals(SecurityUtils.medicoIdActual())) {
            throw new AccessDeniedException("No tiene permiso sobre esta cita");
        }
    }
}

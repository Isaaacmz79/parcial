package com.desarrolloweb.medicitas.repository;

import com.desarrolloweb.medicitas.model.CitaMedica;
import com.desarrolloweb.medicitas.model.EstadoCita;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CitaMedicaRepository extends JpaRepository<CitaMedica, Long> {
    boolean existsByPaciente_Id(Long pacienteId);

    boolean existsByMedico_Id(Long medicoId);

    boolean existsByMedico_IdAndFechaHoraAfterAndEstado(Long medicoId, LocalDateTime fechaHora, EstadoCita estado);
}

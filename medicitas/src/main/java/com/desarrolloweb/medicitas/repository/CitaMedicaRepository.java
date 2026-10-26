package com.desarrolloweb.medicitas.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.desarrolloweb.medicitas.model.CitaMedica;
import com.desarrolloweb.medicitas.model.EstadoCita;
import com.desarrolloweb.medicitas.model.Paciente;

public interface CitaMedicaRepository
        extends JpaRepository<CitaMedica, Long>, JpaSpecificationExecutor<CitaMedica> {

    boolean existsByPaciente_Id(Long pacienteId);

    boolean existsByMedico_Id(Long medicoId);

    boolean existsByMedico_IdAndFechaHoraAfterAndEstado(Long medicoId, LocalDateTime fechaHora, EstadoCita estado);

    boolean existsByPaciente_IdAndMedico_Id(Long pacienteId, Long medicoId);

    @Query("SELECT DISTINCT c.paciente FROM CitaMedica c WHERE c.medico.id = :medicoId")
    List<Paciente> findPacientesByMedicoId(@Param("medicoId") Long medicoId);
}

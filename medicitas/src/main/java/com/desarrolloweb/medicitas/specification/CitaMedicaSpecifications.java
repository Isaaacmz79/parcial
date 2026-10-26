package com.desarrolloweb.medicitas.specification;

import com.desarrolloweb.medicitas.dto.CitaMedicaFiltroDTO;
import com.desarrolloweb.medicitas.model.CitaMedica;
import com.desarrolloweb.medicitas.model.EstadoCita;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

public final class CitaMedicaSpecifications {

    private CitaMedicaSpecifications() {
    }

    /**
     * Combina (AND) unicamente los filtros que vienen con valor.
     * Si no llega ninguno, devuelve todas las citas.
     */
    public static Specification<CitaMedica> conFiltros(CitaMedicaFiltroDTO f) {

        Specification<CitaMedica> spec = (root, query, cb) -> cb.conjunction();

        if (f.estado() != null) {
            spec = spec.and(porEstado(f.estado()));
        }
        if (f.medicoId() != null) {
            spec = spec.and(porMedicoId(f.medicoId()));
        }
        if (f.pacienteId() != null) {
            spec = spec.and(porPacienteId(f.pacienteId()));
        }
        if (StringUtils.hasText(f.especialidad())) {
            spec = spec.and(porEspecialidad(f.especialidad()));
        }
        if (f.fechaDesde() != null) {
            spec = spec.and(fechaDesde(f.fechaDesde()));
        }
        if (f.fechaHasta() != null) {
            spec = spec.and(fechaHasta(f.fechaHasta()));
        }
        if (StringUtils.hasText(f.nombrePaciente())) {
            spec = spec.and(nombrePacienteContiene(f.nombrePaciente()));
        }

        return spec;
    }

    private static Specification<CitaMedica> porEstado(EstadoCita estado) {
        return (root, query, cb) -> cb.equal(root.get("estado"), estado);
    }

    private static Specification<CitaMedica> porMedicoId(Long medicoId) {
        return (root, query, cb) -> cb.equal(root.get("medico").get("id"), medicoId);
    }

    private static Specification<CitaMedica> porPacienteId(Long pacienteId) {
        return (root, query, cb) -> cb.equal(root.get("paciente").get("id"), pacienteId);
    }

    private static Specification<CitaMedica> porEspecialidad(String especialidad) {
        String valor = especialidad.trim().toLowerCase();
        return (root, query, cb) -> cb.equal(
                cb.lower(root.get("medico").get("especialidad")), valor);
    }

    private static Specification<CitaMedica> fechaDesde(LocalDateTime desde) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("fechaHora"), desde);
    }

    private static Specification<CitaMedica> fechaHasta(LocalDateTime hasta) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("fechaHora"), hasta);
    }

    private static Specification<CitaMedica> nombrePacienteContiene(String nombre) {
        String patron = "%" + nombre.trim().toLowerCase() + "%";
        return (root, query, cb) -> cb.like(
                cb.lower(root.get("paciente").get("nombre")), patron);
    }
}

package com.desarrolloweb.medicitas.repository;

import com.desarrolloweb.medicitas.model.NombreRol;
import com.desarrolloweb.medicitas.model.Rol;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RolRepository extends JpaRepository<Rol, Long> {

    Optional<Rol> findByNombre(NombreRol nombre);
}

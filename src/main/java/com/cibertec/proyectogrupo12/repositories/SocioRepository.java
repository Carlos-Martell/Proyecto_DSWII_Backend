package com.cibertec.proyectogrupo12.repositories;

import com.cibertec.proyectogrupo12.entities.Socio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface SocioRepository extends JpaRepository<Socio, Long> {

    Optional<Socio> findByCodigo(String codigo);

    boolean existsByCodigo(String codigo);

    boolean existsByCodigoAndIdNot(String codigo, Long id);

    List<Socio> findByEtapaIn(List<Integer> etapas);

    List<Socio> findByNombresContainingIgnoreCaseOrApellidosContainingIgnoreCase(String nombres, String apellidos);
}

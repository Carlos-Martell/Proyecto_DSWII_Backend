package com.cibertec.proyectogrupo12.repositories;

import com.cibertec.proyectogrupo12.entities.Puesto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface PuestoRepository extends JpaRepository<Puesto, Long> {

    Optional<Puesto> findByNumero(String numero);

    boolean existsByNumero(String numero);

    boolean existsByNumeroAndIdNot(String numero, Long id);

    List<Puesto> findBySocioId(Long socioId);

    List<Puesto> findByGiroId(Long giroId);
}

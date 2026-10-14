package com.cibertec.proyectogrupo12.repositories;

import com.cibertec.proyectogrupo12.entities.Giro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface GiroRepository extends JpaRepository<Giro, Long> {

    Optional<Giro> findByNombre(String nombre);

    boolean existsByNombre(String nombre);

    boolean existsByNombreAndIdNot(String nombre, Long id);
}

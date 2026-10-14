package com.cibertec.proyectogrupo12.repositories;

import com.cibertec.proyectogrupo12.entities.Egreso;
import com.cibertec.proyectogrupo12.entities.enums.EstadoEgreso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;


@Repository
public interface EgresoRepository extends JpaRepository<Egreso, Long> {

    List<Egreso> findByFechaBetween(LocalDate desde, LocalDate hasta);

    List<Egreso> findByEstado(EstadoEgreso estado);

    List<Egreso> findByNumeroDocumento(String numeroDocumento);
}

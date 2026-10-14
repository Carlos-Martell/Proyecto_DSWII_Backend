package com.cibertec.proyectogrupo12.repositories;

import com.cibertec.proyectogrupo12.entities.CuentaPorCobrar;
import com.cibertec.proyectogrupo12.entities.enums.EstadoCuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;


@Repository
public interface CuentaPorCobrarRepository extends JpaRepository<CuentaPorCobrar, Long> {

    List<CuentaPorCobrar> findBySocioId(Long socioId);

    List<CuentaPorCobrar> findByPuestoId(Long puestoId);

    List<CuentaPorCobrar> findBySocioIdAndEstado(Long socioId, EstadoCuenta estado);

    List<CuentaPorCobrar> findByPuestoIdAndEstado(Long puestoId, EstadoCuenta estado);

    List<CuentaPorCobrar> findByServicioId(Long servicioId);

    List<CuentaPorCobrar> findByEstado(EstadoCuenta estado);

    List<CuentaPorCobrar> findBySocioIsNotNullAndFechaGeneracionBetween(LocalDate desde, LocalDate hasta);

    List<CuentaPorCobrar> findByPuestoIsNotNullAndFechaGeneracionBetween(LocalDate desde, LocalDate hasta);
}

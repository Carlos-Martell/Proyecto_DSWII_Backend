package com.cibertec.proyectogrupo12.repositories;

import com.cibertec.proyectogrupo12.entities.Recibo;
import com.cibertec.proyectogrupo12.entities.enums.TipoRecibo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Repository
public interface ReciboRepository extends JpaRepository<Recibo, Long> {

    Optional<Recibo> findByCorrelativo(String correlativo);

    Optional<Recibo> findTopByTipoOrderByIdDesc(TipoRecibo tipo);

    List<Recibo> findByTipoAndFechaBetween(TipoRecibo tipo, LocalDateTime desde, LocalDateTime hasta);

    List<Recibo> findByFechaBetween(LocalDateTime desde, LocalDateTime hasta);

    List<Recibo> findByCuentaId(Long cuentaId);

    List<Recibo> findByCuentaIdIn(List<Long> cuentaIds);
}

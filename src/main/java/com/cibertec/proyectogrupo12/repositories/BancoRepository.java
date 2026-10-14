package com.cibertec.proyectogrupo12.repositories;

import com.cibertec.proyectogrupo12.entities.Banco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface BancoRepository extends JpaRepository<Banco, Long> {
}

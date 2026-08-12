package com.arquisoft.fichas.infrastructure.estadoficha.query.secondaryadapter.repository;

import com.arquisoft.fichas.application.estadoficha.command.secondaryport.entity.EstadoFichaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EstadoFichaQueryRepository extends JpaRepository<EstadoFichaEntity, String> {

    Optional<EstadoFichaEntity> findByNombre(String nombre);
}

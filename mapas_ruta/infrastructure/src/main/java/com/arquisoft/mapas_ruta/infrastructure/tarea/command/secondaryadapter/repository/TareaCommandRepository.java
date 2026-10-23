package com.arquisoft.mapas_ruta.infrastructure.tarea.command.secondaryadapter.repository;

import com.arquisoft.mapas_ruta.infrastructure.tarea.command.secondaryadapter.entity.TareaJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TareaCommandRepository extends JpaRepository<TareaJpaEntity, UUID> {

    boolean existsByMapaRutaIdAndDescripcion(UUID mapaRutaId, String descripcion);
}

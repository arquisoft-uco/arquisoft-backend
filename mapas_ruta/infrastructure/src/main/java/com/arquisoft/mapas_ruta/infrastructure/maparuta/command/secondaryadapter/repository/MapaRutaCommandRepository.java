package com.arquisoft.mapas_ruta.infrastructure.maparuta.command.secondaryadapter.repository;

import com.arquisoft.mapas_ruta.infrastructure.maparuta.command.secondaryadapter.entity.MapaRutaJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MapaRutaCommandRepository extends JpaRepository<MapaRutaJpaEntity, UUID> {

    boolean existsByProyectoGradoId(UUID proyectoGradoId);
}

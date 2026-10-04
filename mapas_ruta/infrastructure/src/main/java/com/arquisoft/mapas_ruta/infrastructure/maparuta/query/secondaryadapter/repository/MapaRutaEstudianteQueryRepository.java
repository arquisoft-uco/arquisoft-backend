package com.arquisoft.mapas_ruta.infrastructure.maparuta.query.secondaryadapter.repository;

import com.arquisoft.shared.jpa.repository.QueryRepository;

import java.util.Optional;
import java.util.UUID;

public interface MapaRutaEstudianteQueryRepository
        extends QueryRepository<MapaRutaEstudianteJpaQueryEntity, UUID> {

    Optional<MapaRutaEstudianteJpaQueryEntity> findByProyectoGradoId(UUID proyectoGradoId);
}

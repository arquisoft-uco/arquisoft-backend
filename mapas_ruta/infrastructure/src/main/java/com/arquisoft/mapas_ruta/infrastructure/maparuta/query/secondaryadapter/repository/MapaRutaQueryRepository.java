package com.arquisoft.mapas_ruta.infrastructure.maparuta.query.secondaryadapter.repository;

import com.arquisoft.shared.jpa.repository.SpecificationQueryRepository;

import java.util.UUID;

public interface MapaRutaQueryRepository
        extends SpecificationQueryRepository<MapaRutaJpaQueryEntity, UUID> {
}

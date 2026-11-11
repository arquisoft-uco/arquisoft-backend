package com.arquisoft.entregables.infrastructure.estadoentregableproyectogrado.query.secondaryadapter.repository;

import com.arquisoft.shared.jpa.repository.QueryRepository;

import java.util.List;
import java.util.UUID;

public interface EstadoEntregableProyectoGradoQueryRepository
        extends QueryRepository<EstadoEntregableProyectoGradoJpaQueryEntity, UUID> {

    List<EstadoEntregableProyectoGradoJpaQueryEntity> findByEntregableProyectoGradoIdOrderByFechaActualizacionAsc(
            UUID entregableProyectoGradoId);
}

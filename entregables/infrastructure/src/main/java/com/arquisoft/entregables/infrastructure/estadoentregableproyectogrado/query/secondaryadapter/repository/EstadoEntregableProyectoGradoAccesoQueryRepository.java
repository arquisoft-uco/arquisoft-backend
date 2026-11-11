package com.arquisoft.entregables.infrastructure.estadoentregableproyectogrado.query.secondaryadapter.repository;

import com.arquisoft.shared.jpa.repository.QueryRepository;

import java.util.Optional;
import java.util.UUID;

public interface EstadoEntregableProyectoGradoAccesoQueryRepository
        extends QueryRepository<EstadoEntregableProyectoGradoAccesoJpaQueryEntity, UUID> {

    Optional<EstadoEntregableProyectoGradoAccesoJpaQueryEntity>
            findFirstByEntregableProyectoGradoIdOrderByFechaActualizacionDesc(UUID entregableProyectoGradoId);
}

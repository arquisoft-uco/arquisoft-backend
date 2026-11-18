package com.arquisoft.entregables.infrastructure.entregableproyectogrado.query.secondaryadapter.repository;

import com.arquisoft.shared.jpa.repository.QueryRepository;

import java.util.UUID;

public interface EstudianteEntregableAccesoQueryRepository
        extends QueryRepository<EstudianteEntregableAccesoJpaQueryEntity, UUID> {

    boolean existsByEntregableProyectoGradoIdAndEstudianteId(UUID entregableProyectoGradoId, UUID estudianteId);
}

package com.arquisoft.entregables.infrastructure.proyectogrado.query.secondaryadapter.repository;

import com.arquisoft.shared.jpa.repository.QueryRepository;

import java.util.UUID;

public interface EstudianteProyectoGradoAccesoQueryRepository
        extends QueryRepository<EstudianteProyectoGradoAccesoJpaQueryEntity, UUID> {

    boolean existsByEstudianteIdAndProyectoGradoId(UUID estudianteId, UUID proyectoGradoId);
}

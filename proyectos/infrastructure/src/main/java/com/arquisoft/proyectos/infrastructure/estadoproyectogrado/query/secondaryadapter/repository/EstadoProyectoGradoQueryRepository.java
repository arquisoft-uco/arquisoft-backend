package com.arquisoft.proyectos.infrastructure.estadoproyectogrado.query.secondaryadapter.repository;

import com.arquisoft.shared.jpa.repository.QueryRepository;

import java.util.List;
import java.util.UUID;

public interface EstadoProyectoGradoQueryRepository
        extends QueryRepository<EstadoProyectoGradoJpaQueryEntity, UUID> {

    List<EstadoProyectoGradoJpaQueryEntity> findByProyectoGradoIdOrderByFechaActualizacionDescIdDesc(
            UUID proyectoGradoId);
}

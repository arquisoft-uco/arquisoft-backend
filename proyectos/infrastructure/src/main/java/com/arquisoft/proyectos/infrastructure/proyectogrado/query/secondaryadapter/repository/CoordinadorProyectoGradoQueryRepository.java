package com.arquisoft.proyectos.infrastructure.proyectogrado.query.secondaryadapter.repository;

import com.arquisoft.shared.jpa.repository.QueryRepository;

import java.util.List;
import java.util.UUID;

public interface CoordinadorProyectoGradoQueryRepository
        extends QueryRepository<CoordinadorProyectoGradoJpaQueryEntity, UUID> {

    List<CoordinadorProyectoGradoJpaQueryEntity> findByEstudianteIdOrderByTituloProyectoAscProyectoGradoIdAsc(
            UUID estudianteId);
}

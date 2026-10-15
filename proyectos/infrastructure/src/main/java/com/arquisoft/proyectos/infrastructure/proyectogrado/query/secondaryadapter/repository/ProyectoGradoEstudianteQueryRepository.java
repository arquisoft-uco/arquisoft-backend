package com.arquisoft.proyectos.infrastructure.proyectogrado.query.secondaryadapter.repository;

import com.arquisoft.shared.jpa.repository.QueryRepository;

import java.util.List;
import java.util.UUID;

public interface ProyectoGradoEstudianteQueryRepository
        extends QueryRepository<ProyectoGradoEstudianteJpaQueryEntity, UUID> {

    List<ProyectoGradoEstudianteJpaQueryEntity> findByEstudianteIdOrderByTituloProyectoAscProyectoGradoIdAsc(
            UUID estudianteId);
}

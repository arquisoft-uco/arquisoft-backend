package com.arquisoft.proyectos.infrastructure.estudianteproyectogrado.query.secondaryadapter.repository;

import com.arquisoft.shared.jpa.repository.QueryRepository;

import java.util.List;
import java.util.UUID;

public interface EstudianteProyectoGradoQueryRepository
        extends QueryRepository<EstudianteProyectoGradoJpaQueryEntity, UUID> {

    List<EstudianteProyectoGradoJpaQueryEntity> findByProyectoGradoIdOrderByNombreAscIdAsc(UUID proyectoGradoId);
}

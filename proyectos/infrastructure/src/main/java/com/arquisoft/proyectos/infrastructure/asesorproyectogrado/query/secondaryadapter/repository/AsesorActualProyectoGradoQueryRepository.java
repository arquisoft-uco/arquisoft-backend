package com.arquisoft.proyectos.infrastructure.asesorproyectogrado.query.secondaryadapter.repository;

import com.arquisoft.shared.jpa.repository.QueryRepository;

import java.util.List;
import java.util.UUID;

public interface AsesorActualProyectoGradoQueryRepository
        extends QueryRepository<AsesorActualProyectoGradoJpaQueryEntity, UUID> {

    List<AsesorActualProyectoGradoJpaQueryEntity> findByEstudianteIdOrderByTituloProyectoAscProyectoGradoIdAsc(
            UUID estudianteId);
}

package com.arquisoft.proyectos.infrastructure.proyectogrado.query.secondaryadapter.repository;

import com.arquisoft.shared.jpa.repository.SpecificationQueryRepository;

import java.util.UUID;

public interface ProyectoGradoQueryRepository
        extends SpecificationQueryRepository<ProyectoGradoListadoJpaQueryEntity, UUID> {
}

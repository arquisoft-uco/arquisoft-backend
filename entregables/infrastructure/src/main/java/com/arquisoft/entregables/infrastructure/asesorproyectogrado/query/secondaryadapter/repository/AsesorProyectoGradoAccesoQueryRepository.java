package com.arquisoft.entregables.infrastructure.asesorproyectogrado.query.secondaryadapter.repository;

import com.arquisoft.shared.jpa.repository.QueryRepository;

import java.util.Optional;
import java.util.UUID;

public interface AsesorProyectoGradoAccesoQueryRepository
        extends QueryRepository<AsesorProyectoGradoAccesoJpaQueryEntity, UUID> {

    Optional<AsesorProyectoGradoAccesoJpaQueryEntity> findFirstByProyectoGradoIdOrderByOcurridoEnDescAsesorIdAsc(
            UUID proyectoGradoId);
}

package com.arquisoft.artefactos.infrastructure.observacion.command.secondaryadapter.repository;

import com.arquisoft.artefactos.infrastructure.observacion.command.secondaryadapter.entity.ObservacionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ObservacionCommandRepository extends JpaRepository<ObservacionJpaEntity, UUID> {

    boolean existsByRevisionAsesorAndDescripcion(UUID revisionAsesor, String descripcion);
}

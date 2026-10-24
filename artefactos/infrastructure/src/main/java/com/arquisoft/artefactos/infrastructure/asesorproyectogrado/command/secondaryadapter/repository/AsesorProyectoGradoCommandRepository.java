package com.arquisoft.artefactos.infrastructure.asesorproyectogrado.command.secondaryadapter.repository;

import com.arquisoft.artefactos.infrastructure.asesorproyectogrado.command.secondaryadapter.entity.AsesorProyectoGradoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AsesorProyectoGradoCommandRepository extends JpaRepository<AsesorProyectoGradoJpaEntity, UUID> {

    Optional<AsesorProyectoGradoJpaEntity> findByAsesorIdAndProyectoGradoId(UUID asesorId, UUID proyectoGradoId);
}

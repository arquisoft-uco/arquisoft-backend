package com.arquisoft.proyectos.infrastructure.asesorproyectogrado.command.secondaryadapter.repository;

import com.arquisoft.proyectos.infrastructure.asesorproyectogrado.command.secondaryadapter.entity.AsesorProyectoGradoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AsesorProyectoGradoCommandRepository extends JpaRepository<AsesorProyectoGradoJpaEntity, UUID> {

    boolean existsByAsesorIdAndProyectoGradoIdAndEstadoAsesorId(
            UUID asesorId, UUID proyectoGradoId, String estadoAsesorId);

    long countByProyectoGradoIdAndEstadoAsesorId(UUID proyectoGradoId, String estadoAsesorId);
}

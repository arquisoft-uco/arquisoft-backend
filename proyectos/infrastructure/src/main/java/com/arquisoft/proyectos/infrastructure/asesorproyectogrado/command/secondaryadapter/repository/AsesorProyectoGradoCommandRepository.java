package com.arquisoft.proyectos.infrastructure.asesorproyectogrado.command.secondaryadapter.repository;

import com.arquisoft.proyectos.infrastructure.asesorproyectogrado.command.secondaryadapter.entity.AsesorProyectoGradoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface AsesorProyectoGradoCommandRepository extends JpaRepository<AsesorProyectoGradoJpaEntity, UUID> {

    boolean existsByAsesorIdAndProyectoGradoIdAndEstadoAsesorId(
            UUID asesorId, UUID proyectoGradoId, String estadoAsesorId);

    long countByProyectoGradoIdAndEstadoAsesorId(UUID proyectoGradoId, String estadoAsesorId);

    Optional<AsesorProyectoGradoJpaEntity> findByIdAndProyectoGradoId(UUID id, UUID proyectoGradoId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE AsesorProyectoGradoJpaEntity a SET a.estadoAsesorId = :estadoAsesor WHERE a.id = :id")
    int actualizarEstadoAsesor(@Param("id") UUID id, @Param("estadoAsesor") String estadoAsesor);
}

package com.arquisoft.entregables.infrastructure.asesorproyectogrado.command.secondaryadapter.repository;

import com.arquisoft.entregables.infrastructure.asesorproyectogrado.command.secondaryadapter.entity.AsesorProyectoGradoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AsesorProyectoGradoCommandRepository extends JpaRepository<AsesorProyectoGradoJpaEntity, UUID> {

    Optional<AsesorProyectoGradoJpaEntity> findByAsesorIdAndProyectoGradoId(UUID asesorId, UUID proyectoGradoId);

    @Query("SELECT a.asesorId FROM AsesorProyectoGradoJpaEntity a WHERE a.proyectoGradoId = :proyectoGradoId "
            + "AND a.finalizadoEn IS NULL "
            + "AND a.ocurridoEn = (SELECT MAX(b.ocurridoEn) FROM AsesorProyectoGradoJpaEntity b "
            + "WHERE b.proyectoGradoId = :proyectoGradoId)")
    List<UUID> findAsesorVigenteByProyectoGradoId(@Param("proyectoGradoId") UUID proyectoGradoId);

    @Query("SELECT a.asesorId FROM AsesorProyectoGradoJpaEntity a "
            + "WHERE a.proyectoGradoId = (SELECT e.proyectoGrado FROM EntregableProyectoGradoJpaEntity e "
            + "WHERE e.id = :entregableId) "
            + "AND a.finalizadoEn IS NULL "
            + "AND a.ocurridoEn = (SELECT MAX(b.ocurridoEn) FROM AsesorProyectoGradoJpaEntity b "
            + "WHERE b.proyectoGradoId = a.proyectoGradoId)")
    List<UUID> findAsesorVigenteByEntregableId(@Param("entregableId") UUID entregableId);
}

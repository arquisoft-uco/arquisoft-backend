package com.arquisoft.artefactos.infrastructure.asesorproyectogrado.command.secondaryadapter.repository;

import com.arquisoft.artefactos.infrastructure.asesorproyectogrado.command.secondaryadapter.entity.AsesorProyectoGradoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AsesorProyectoGradoCommandRepository extends JpaRepository<AsesorProyectoGradoJpaEntity, UUID> {

    @Query("SELECT MAX(a.ocurridoEn) FROM AsesorProyectoGradoJpaEntity a WHERE a.proyectoGradoId = :proyectoGradoId")
    Optional<Instant> findUltimoOcurridoEnByProyectoGradoId(@Param("proyectoGradoId") UUID proyectoGradoId);

    @Query("SELECT a FROM AsesorProyectoGradoJpaEntity a WHERE a.proyectoGradoId = :proyectoGradoId "
            + "AND a.ocurridoEn = (SELECT MAX(b.ocurridoEn) FROM AsesorProyectoGradoJpaEntity b "
            + "WHERE b.proyectoGradoId = :proyectoGradoId)")
    List<AsesorProyectoGradoJpaEntity> findMasRecientesByProyectoGradoId(@Param("proyectoGradoId") UUID proyectoGradoId);

    Optional<AsesorProyectoGradoJpaEntity> findByAsesorIdAndProyectoGradoId(UUID asesorId, UUID proyectoGradoId);
}

package com.arquisoft.mapas_ruta.infrastructure.proyectogrado.command.secondaryadapter.repository;

import com.arquisoft.mapas_ruta.infrastructure.proyectogrado.command.secondaryadapter.entity.ProyectoGradoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.UUID;

public interface ProyectoGradoCommandRepository extends JpaRepository<ProyectoGradoJpaEntity, UUID> {

    @Modifying(clearAutomatically = true)
    @Query("UPDATE ProyectoGradoJpaEntity p SET p.estadoProyectoGradoId = :estado, p.ocurridoEn = :ocurridoEn "
            + "WHERE p.id = :id AND (p.ocurridoEn IS NULL OR p.ocurridoEn < :ocurridoEn)")
    int actualizarEstado(@Param("id") UUID id, @Param("estado") String estado,
                         @Param("ocurridoEn") Instant ocurridoEn);
}

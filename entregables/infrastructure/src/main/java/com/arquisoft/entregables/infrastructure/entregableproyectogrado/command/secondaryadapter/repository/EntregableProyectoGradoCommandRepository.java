package com.arquisoft.entregables.infrastructure.entregableproyectogrado.command.secondaryadapter.repository;

import com.arquisoft.entregables.infrastructure.entregableproyectogrado.command.secondaryadapter.entity.EntregableProyectoGradoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface EntregableProyectoGradoCommandRepository
        extends JpaRepository<EntregableProyectoGradoJpaEntity, UUID> {

    @Query("SELECT COALESCE(MAX(e.version), 0) FROM EntregableProyectoGradoJpaEntity e "
            + "WHERE e.proyectoGrado = :proyectoGrado")
    int obtenerUltimaVersion(@Param("proyectoGrado") UUID proyectoGrado);
}

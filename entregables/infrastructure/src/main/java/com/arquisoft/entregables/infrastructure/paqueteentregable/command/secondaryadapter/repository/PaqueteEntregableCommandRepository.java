package com.arquisoft.entregables.infrastructure.paqueteentregable.command.secondaryadapter.repository;

import com.arquisoft.entregables.infrastructure.paqueteentregable.command.secondaryadapter.entity.PaqueteEntregableJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface PaqueteEntregableCommandRepository extends JpaRepository<PaqueteEntregableJpaEntity, UUID> {

    @NativeQuery("""
            SELECT p.*
            FROM paquete_entregable p
            JOIN entregable_proyecto_grado e ON e.id = p.entregable_proyecto_grado_id
            WHERE e.proyecto_grado_id = :proyectoGrado
            """)
    List<PaqueteEntregableJpaEntity> buscarPorProyectoGrado(@Param("proyectoGrado") UUID proyectoGrado);
}

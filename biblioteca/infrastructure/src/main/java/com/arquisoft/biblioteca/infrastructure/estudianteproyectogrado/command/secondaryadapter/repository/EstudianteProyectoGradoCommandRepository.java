package com.arquisoft.biblioteca.infrastructure.estudianteproyectogrado.command.secondaryadapter.repository;

import com.arquisoft.biblioteca.infrastructure.estudianteproyectogrado.command.secondaryadapter.entity.EstudianteProyectoGradoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EstudianteProyectoGradoCommandRepository
        extends JpaRepository<EstudianteProyectoGradoJpaEntity, UUID> {

    List<EstudianteProyectoGradoJpaEntity> findByProyectoGradoId(UUID proyectoGradoId);

    Optional<EstudianteProyectoGradoJpaEntity> findByAutorIdAndProyectoGradoId(UUID autorId, UUID proyectoGradoId);

    @Query("""
            SELECT new com.arquisoft.biblioteca.infrastructure.estudianteproyectogrado.command.secondaryadapter.repository.AutorDeEntregableFila(
                       epg.autorId,
                       CASE WHEN a.id IS NOT NULL AND a.eliminadoEn IS NULL THEN true ELSE false END)
            FROM EstudianteProyectoGradoJpaEntity epg
            JOIN EntregableProyectoGradoJpaEntity e ON e.proyectoGradoId = epg.proyectoGradoId
            LEFT JOIN AutorJpaEntity a ON a.id = epg.autorId
            WHERE e.id = :entregable AND epg.eliminadoEn IS NULL
            """)
    List<AutorDeEntregableFila> consultarAutoresPorEntregable(@Param("entregable") UUID entregable);
}

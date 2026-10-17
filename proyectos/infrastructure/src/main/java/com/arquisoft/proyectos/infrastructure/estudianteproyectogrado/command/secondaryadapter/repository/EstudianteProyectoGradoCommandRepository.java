package com.arquisoft.proyectos.infrastructure.estudianteproyectogrado.command.secondaryadapter.repository;

import com.arquisoft.proyectos.infrastructure.estudianteproyectogrado.command.secondaryadapter.entity.EstudianteProyectoGradoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface EstudianteProyectoGradoCommandRepository extends JpaRepository<EstudianteProyectoGradoJpaEntity, UUID> {

    long countByProyectoGradoId(UUID proyectoGradoId);

    boolean existsByProyectoGradoIdAndEstudianteId(UUID proyectoGradoId, UUID estudianteId);

    @Modifying
    @Query("DELETE FROM EstudianteProyectoGradoJpaEntity e "
            + "WHERE e.proyectoGradoId = :proyectoGrado AND e.estudianteId = :estudiante")
    void eliminarPorProyectoGradoIdYEstudianteId(
            @Param("proyectoGrado") UUID proyectoGrado, @Param("estudiante") UUID estudiante);

    @Query("SELECT e.estudianteId FROM EstudianteProyectoGradoJpaEntity e "
            + "WHERE e.proyectoGradoId = :proyectoGrado AND e.estudianteId IN :estudiantes")
    List<UUID> findEstudianteIdsByProyectoGradoIdAndEstudianteIdIn(
            @Param("proyectoGrado") UUID proyectoGrado, @Param("estudiantes") Collection<UUID> estudiantes);
}

package com.arquisoft.fichas.infrastructure.estudiantefichaperfil.command.secondaryadapter.repository;

import com.arquisoft.fichas.infrastructure.estudiantefichaperfil.command.secondaryadapter.entity.EstudianteFichaPerfilJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface EstudianteFichaPerfilCommandRepository
        extends JpaRepository<EstudianteFichaPerfilJpaEntity, UUID> {

    boolean existsByFichaPerfilIdAndEstudianteId(UUID fichaPerfilId, UUID estudianteId);

    long countByFichaPerfilId(UUID fichaPerfilId);

    void deleteByFichaPerfilIdAndEstudianteId(UUID fichaPerfilId, UUID estudianteId);

    // Derivado por nombre (findEstudianteIdBy...) falla al convertir la proyección escalar a
    // List<UUID> en esta versión de Spring Data; @Query explícito evita el problema.
    @Query("SELECT e.estudianteId FROM EstudianteFichaPerfilJpaEntity e WHERE e.fichaPerfilId = :fichaPerfilId")
    List<UUID> findEstudianteIdByFichaPerfilId(@Param("fichaPerfilId") UUID fichaPerfilId);
}

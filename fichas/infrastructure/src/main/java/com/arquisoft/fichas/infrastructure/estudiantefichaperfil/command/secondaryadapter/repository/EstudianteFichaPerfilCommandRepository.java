package com.arquisoft.fichas.infrastructure.estudiantefichaperfil.command.secondaryadapter.repository;

import com.arquisoft.fichas.infrastructure.estudiantefichaperfil.command.secondaryadapter.entity.EstudianteFichaPerfilJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EstudianteFichaPerfilCommandRepository
        extends JpaRepository<EstudianteFichaPerfilJpaEntity, UUID> {

    boolean existsByFichaPerfilIdAndEstudianteId(UUID fichaPerfilId, UUID estudianteId);

    long countByFichaPerfilId(UUID fichaPerfilId);

    void deleteByFichaPerfilIdAndEstudianteId(UUID fichaPerfilId, UUID estudianteId);
}

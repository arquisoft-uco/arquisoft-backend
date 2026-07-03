package com.arquisoft.fichas.infrastructure.estudiantefichaperfil.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EstudianteFichaPerfilJpaRepository extends JpaRepository<EstudianteFichaPerfilJpaEntity, UUID> {

    boolean existsByFichaPerfilIdAndEstudianteId(UUID fichaPerfilId, UUID estudianteId);

    long countByFichaPerfilId(UUID fichaPerfilId);
}

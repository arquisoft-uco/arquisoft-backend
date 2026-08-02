package com.arquisoft.fichas.infrastructure.estudiantefichaperfil.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EstudianteFichaPerfilRepository extends JpaRepository<EstudianteFichaPerfilEntity, UUID> {

    boolean existsByFichaPerfilIdAndEstudianteId(UUID fichaPerfilId, UUID estudianteId);

    long countByFichaPerfilId(UUID fichaPerfilId);

    void deleteByFichaPerfilIdAndEstudianteId(UUID fichaPerfilId, UUID estudianteId);
}

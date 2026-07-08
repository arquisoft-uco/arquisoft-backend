package com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EvaluacionFichaPerfilJpaRepository
        extends JpaRepository<EvaluacionFichaPerfilJpaEntity, UUID> {

    boolean existsByRepresentanteComiteIdAndFichaPerfilId(
            UUID representanteComiteId,
            UUID fichaPerfilId);
}

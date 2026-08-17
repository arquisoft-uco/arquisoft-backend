package com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.command.secondaryadapter.repository;

import com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.command.secondaryadapter.entity.EvaluacionFichaPerfilJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EvaluacionFichaPerfilCommandRepository
        extends JpaRepository<EvaluacionFichaPerfilJpaEntity, UUID> {

    boolean existsByRepresentanteComiteIdAndFichaPerfilId(
            UUID representanteComiteId,
            UUID fichaPerfilId);

    boolean existsByIdAndRepresentanteComiteId(
            UUID id,
            UUID representanteComiteId);
}

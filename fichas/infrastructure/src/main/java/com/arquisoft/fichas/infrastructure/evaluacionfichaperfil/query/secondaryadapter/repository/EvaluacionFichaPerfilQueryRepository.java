package com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.query.secondaryadapter.repository;

import com.arquisoft.fichas.application.evaluacionfichaperfil.command.secondaryport.entity.EvaluacionFichaPerfilEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EvaluacionFichaPerfilQueryRepository
        extends JpaRepository<EvaluacionFichaPerfilEntity, UUID> {

    boolean existsByIdAndRepresentanteComiteId(
            UUID id,
            UUID representanteComiteId);
}

package com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.command.secondaryadapter.repository;

import com.arquisoft.fichas.application.evaluacionfichaperfil.command.secondaryport.entity.EvaluacionFichaPerfilEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EvaluacionFichaPerfilCommandRepository
        extends JpaRepository<EvaluacionFichaPerfilEntity, UUID> {

    boolean existsByRepresentanteComiteIdAndFichaPerfilId(
            UUID representanteComiteId,
            UUID fichaPerfilId);

    boolean existsByIdAndRepresentanteComiteId(
            UUID id,
            UUID representanteComiteId);
}

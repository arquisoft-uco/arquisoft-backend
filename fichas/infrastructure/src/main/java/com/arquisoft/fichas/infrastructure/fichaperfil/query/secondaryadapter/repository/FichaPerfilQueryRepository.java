package com.arquisoft.fichas.infrastructure.fichaperfil.query.secondaryadapter.repository;

import com.arquisoft.shared.postgres.repository.RepositorioSoloLecturaConEspecificacion;

import java.util.UUID;

public interface FichaPerfilQueryRepository
        extends RepositorioSoloLecturaConEspecificacion<FichaPerfilJpaQueryEntity, UUID> {
}

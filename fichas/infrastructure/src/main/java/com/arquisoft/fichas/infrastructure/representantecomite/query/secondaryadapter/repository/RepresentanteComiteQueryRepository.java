package com.arquisoft.fichas.infrastructure.representantecomite.query.secondaryadapter.repository;

import com.arquisoft.fichas.application.representantecomite.command.secondaryport.entity.RepresentanteComiteEntity;
import com.arquisoft.shared.postgres.repository.RepositorioSoloLectura;

import java.util.UUID;

public interface RepresentanteComiteQueryRepository
        extends RepositorioSoloLectura<RepresentanteComiteEntity, UUID> {
}

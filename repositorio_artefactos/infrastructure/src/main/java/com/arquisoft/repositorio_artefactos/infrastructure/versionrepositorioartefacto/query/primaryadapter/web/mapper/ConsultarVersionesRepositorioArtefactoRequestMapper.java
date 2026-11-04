package com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.query.primaryadapter.web.mapper;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.primaryport.model.ConsultarVersionesRepositorioArtefactoQuery;

import java.util.UUID;

public final class ConsultarVersionesRepositorioArtefactoRequestMapper {

    private ConsultarVersionesRepositorioArtefactoRequestMapper() {}

    public static ConsultarVersionesRepositorioArtefactoQuery toQuery(UUID repositorioId) {
        return ConsultarVersionesRepositorioArtefactoQuery.crear(repositorioId);
    }
}

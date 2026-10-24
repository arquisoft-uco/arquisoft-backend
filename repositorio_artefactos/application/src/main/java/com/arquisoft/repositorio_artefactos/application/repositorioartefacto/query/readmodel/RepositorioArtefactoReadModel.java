package com.arquisoft.repositorio_artefactos.application.repositorioartefacto.query.readmodel;

import com.arquisoft.repositorio_artefactos.application.administrador.query.readmodel.AdministradorReadModel;

import java.util.UUID;

public record RepositorioArtefactoReadModel(
        UUID id,
        String nombreRepositorio,
        String descripcion,
        AdministradorReadModel administrador
) {
}

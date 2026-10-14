package com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.secondaryport.entity;

import java.util.UUID;

public record RepositorioArtefactoEntity(
        UUID id,
        String nombreRepositorio,
        String descripcion,
        UUID administrador
) {
}

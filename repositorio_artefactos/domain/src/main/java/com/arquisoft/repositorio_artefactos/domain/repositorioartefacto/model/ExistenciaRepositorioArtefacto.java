package com.arquisoft.repositorio_artefactos.domain.repositorioartefacto.model;

import com.arquisoft.repositorio_artefactos.domain.repositorioartefacto.RepositorioArtefactoDomain;

import java.util.UUID;

public record ExistenciaRepositorioArtefacto(UUID repositorio, RepositorioArtefactoDomain encontrado) {}

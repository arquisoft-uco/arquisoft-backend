package com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.model;

import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.ArchivoVersionDomain;

import java.util.UUID;

public record ExistenciaVersionRepositorioArtefacto(UUID version, ArchivoVersionDomain archivo) {}

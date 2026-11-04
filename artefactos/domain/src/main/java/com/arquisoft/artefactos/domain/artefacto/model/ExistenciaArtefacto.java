package com.arquisoft.artefactos.domain.artefacto.model;

import com.arquisoft.artefactos.domain.artefacto.ArtefactoDomain;

import java.util.UUID;

public record ExistenciaArtefacto(UUID artefacto, ArtefactoDomain encontrado) {}

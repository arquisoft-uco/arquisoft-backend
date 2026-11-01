package com.arquisoft.artefactos.domain.artefacto.model;

import com.arquisoft.artefactos.domain.revisionasesor.model.RevisionVersionAnterior;

import java.util.UUID;

public record MomentoCambioVersionRepositorio(
        UUID artefacto, String documento, RevisionVersionAnterior versionAnterior) {}

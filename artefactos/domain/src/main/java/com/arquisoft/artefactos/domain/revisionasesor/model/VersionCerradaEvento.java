package com.arquisoft.artefactos.domain.revisionasesor.model;

import java.time.Instant;
import java.util.UUID;

public record VersionCerradaEvento(
        UUID proyectoGrado,
        UUID artefacto,
        UUID versionArtefacto,
        String uriArchivo,
        Instant fechaVersion,
        UUID versionRepositorioArtefacto,
        UUID repositorio,
        String nombreRepositorio
) {}

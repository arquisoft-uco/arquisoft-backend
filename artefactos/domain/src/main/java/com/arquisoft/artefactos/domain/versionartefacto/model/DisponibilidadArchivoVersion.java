package com.arquisoft.artefactos.domain.versionartefacto.model;

import java.util.UUID;

public record DisponibilidadArchivoVersion(UUID versionArtefacto, String nombreProyecto, boolean proyectoFinalizado) {}

package com.arquisoft.artefactos.domain.artefacto.model;

import com.arquisoft.artefactos.domain.artefacto.ArtefactoDomain;

import java.util.List;
import java.util.UUID;

public record PropiedadArtefactosAsesor(UUID asesor, List<ArtefactoDomain> artefactos) {}

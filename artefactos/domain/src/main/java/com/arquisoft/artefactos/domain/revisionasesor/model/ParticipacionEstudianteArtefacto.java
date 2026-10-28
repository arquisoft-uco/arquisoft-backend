package com.arquisoft.artefactos.domain.revisionasesor.model;

import java.util.UUID;

public record ParticipacionEstudianteArtefacto(UUID revisionAsesor, UUID estudiante, boolean participa) {}

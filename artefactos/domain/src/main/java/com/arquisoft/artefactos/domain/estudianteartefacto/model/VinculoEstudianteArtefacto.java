package com.arquisoft.artefactos.domain.estudianteartefacto.model;

import java.util.UUID;

public record VinculoEstudianteArtefacto(UUID estudiante, UUID artefacto, String nombreEstudiante, boolean vinculado) {}

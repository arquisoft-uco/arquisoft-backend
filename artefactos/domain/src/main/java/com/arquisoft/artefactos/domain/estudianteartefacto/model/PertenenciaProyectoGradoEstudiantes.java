package com.arquisoft.artefactos.domain.estudianteartefacto.model;

import com.arquisoft.artefactos.domain.estudiante.EstudianteDomain;

import java.util.List;
import java.util.UUID;

public record PertenenciaProyectoGradoEstudiantes(
        List<EstudianteDomain> estudiantes,
        List<UUID> vigentes) {}

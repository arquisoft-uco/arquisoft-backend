package com.arquisoft.artefactos.domain.estudianteartefacto.model;

import com.arquisoft.artefactos.domain.estudiante.EstudianteDomain;

import java.util.List;

public record PertenenciaProyectoGradoEstudiantes(
        List<EstudianteDomain> estudiantes,
        List<ParEstudianteArtefacto> solicitados,
        List<ParEstudianteArtefacto> vigentes) {}

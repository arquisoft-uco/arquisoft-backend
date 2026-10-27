package com.arquisoft.artefactos.domain.estudianteartefacto.model;

import com.arquisoft.artefactos.domain.estudiante.EstudianteDomain;

import java.util.List;

public record VinculosEstudiantesArtefacto(
        List<EstudianteDomain> estudiantes,
        List<ParEstudianteArtefacto> yaVinculados) {}

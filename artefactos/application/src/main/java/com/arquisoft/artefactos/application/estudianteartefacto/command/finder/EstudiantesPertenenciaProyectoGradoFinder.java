package com.arquisoft.artefactos.application.estudianteartefacto.command.finder;

import com.arquisoft.artefactos.domain.estudianteartefacto.AgregacionEstudiantesArtefactoDomain;
import com.arquisoft.artefactos.domain.estudianteartefacto.model.ParEstudianteArtefacto;
import com.arquisoft.shared.finder.Finder;

import java.util.List;

public interface EstudiantesPertenenciaProyectoGradoFinder
        extends Finder<AgregacionEstudiantesArtefactoDomain, List<ParEstudianteArtefacto>> {
}

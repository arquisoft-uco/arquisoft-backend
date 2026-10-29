package com.arquisoft.artefactos.application.estudianteproyectogrado.command.finder;

import com.arquisoft.artefactos.domain.estudianteproyectogrado.AgregacionEstudiantesProyectoGradoDomain;
import com.arquisoft.artefactos.domain.estudianteproyectogrado.EstudianteProyectoGradoDomain;
import com.arquisoft.shared.finder.Finder;

import java.util.List;

public interface EstudiantesProyectoGradoPorVinculosFinder
        extends Finder<AgregacionEstudiantesProyectoGradoDomain, List<EstudianteProyectoGradoDomain>> {
}

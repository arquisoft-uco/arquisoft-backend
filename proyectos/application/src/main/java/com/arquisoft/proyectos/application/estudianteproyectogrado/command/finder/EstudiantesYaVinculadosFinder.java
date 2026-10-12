package com.arquisoft.proyectos.application.estudianteproyectogrado.command.finder;

import com.arquisoft.proyectos.domain.estudianteproyectogrado.AgregacionEstudiantesProyectoGradoDomain;
import com.arquisoft.shared.finder.Finder;

import java.util.List;
import java.util.UUID;

public interface EstudiantesYaVinculadosFinder extends Finder<AgregacionEstudiantesProyectoGradoDomain, List<UUID>> {
}

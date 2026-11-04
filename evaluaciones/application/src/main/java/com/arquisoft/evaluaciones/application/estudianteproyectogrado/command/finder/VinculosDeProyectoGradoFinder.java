package com.arquisoft.evaluaciones.application.estudianteproyectogrado.command.finder;

import com.arquisoft.evaluaciones.domain.estudianteproyectogrado.EstudianteProyectoGradoDomain;
import com.arquisoft.shared.finder.Finder;

import java.util.List;
import java.util.UUID;

public interface VinculosDeProyectoGradoFinder extends Finder<UUID, List<EstudianteProyectoGradoDomain>> {
}

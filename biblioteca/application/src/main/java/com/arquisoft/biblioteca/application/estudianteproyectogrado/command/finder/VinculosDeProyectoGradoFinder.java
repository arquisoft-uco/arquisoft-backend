package com.arquisoft.biblioteca.application.estudianteproyectogrado.command.finder;

import com.arquisoft.biblioteca.domain.estudianteproyectogrado.EstudianteProyectoGradoDomain;
import com.arquisoft.shared.finder.Finder;

import java.util.List;
import java.util.UUID;

public interface VinculosDeProyectoGradoFinder extends Finder<UUID, List<EstudianteProyectoGradoDomain>> {
}

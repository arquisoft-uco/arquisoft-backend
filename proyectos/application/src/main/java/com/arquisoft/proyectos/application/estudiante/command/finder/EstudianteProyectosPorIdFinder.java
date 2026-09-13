package com.arquisoft.proyectos.application.estudiante.command.finder;

import com.arquisoft.proyectos.application.estudiante.command.secondaryport.entity.EstudianteEntity;
import com.arquisoft.shared.finder.Finder;

import java.util.Optional;
import java.util.UUID;

public interface EstudianteProyectosPorIdFinder extends Finder<UUID, Optional<EstudianteEntity>> {
}

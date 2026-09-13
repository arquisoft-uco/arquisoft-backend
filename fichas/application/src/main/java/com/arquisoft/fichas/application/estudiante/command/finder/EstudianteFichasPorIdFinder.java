package com.arquisoft.fichas.application.estudiante.command.finder;

import com.arquisoft.fichas.application.estudiante.command.secondaryport.entity.EstudianteEntity;
import com.arquisoft.shared.finder.Finder;

import java.util.Optional;
import java.util.UUID;

public interface EstudianteFichasPorIdFinder extends Finder<UUID, Optional<EstudianteEntity>> {
}

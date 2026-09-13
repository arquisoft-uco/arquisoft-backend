package com.arquisoft.proyectos.application.asesor.command.finder;

import com.arquisoft.proyectos.application.asesor.command.secondaryport.entity.AsesorEntity;
import com.arquisoft.shared.finder.Finder;

import java.util.Optional;
import java.util.UUID;

public interface AsesorProyectosPorIdFinder extends Finder<UUID, Optional<AsesorEntity>> {
}

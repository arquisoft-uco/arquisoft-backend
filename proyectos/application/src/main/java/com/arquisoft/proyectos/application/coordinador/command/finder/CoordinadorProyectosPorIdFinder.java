package com.arquisoft.proyectos.application.coordinador.command.finder;

import com.arquisoft.proyectos.application.coordinador.command.secondaryport.entity.CoordinadorEntity;
import com.arquisoft.shared.finder.Finder;

import java.util.Optional;
import java.util.UUID;

public interface CoordinadorProyectosPorIdFinder extends Finder<UUID, Optional<CoordinadorEntity>> {
}

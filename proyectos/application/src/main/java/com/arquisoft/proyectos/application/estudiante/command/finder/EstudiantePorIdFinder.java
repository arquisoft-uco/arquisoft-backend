package com.arquisoft.proyectos.application.estudiante.command.finder;

import com.arquisoft.proyectos.domain.estudiante.EstudianteDomain;
import com.arquisoft.shared.finder.Finder;

import java.util.UUID;

public interface EstudiantePorIdFinder extends Finder<UUID, EstudianteDomain> {
}

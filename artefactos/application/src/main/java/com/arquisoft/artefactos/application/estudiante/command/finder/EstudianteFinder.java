package com.arquisoft.artefactos.application.estudiante.command.finder;

import com.arquisoft.artefactos.domain.estudiante.EstudianteDomain;
import com.arquisoft.shared.finder.Finder;

import java.util.UUID;

public interface EstudianteFinder extends Finder<UUID, EstudianteDomain> {
}

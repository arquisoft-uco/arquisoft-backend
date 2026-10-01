package com.arquisoft.usuarios.application.estudiante.command.finder;

import com.arquisoft.shared.finder.Finder;
import com.arquisoft.usuarios.domain.estudiante.EstudianteDomain;

import java.util.UUID;

public interface EstudiantePorUsuarioFinder extends Finder<UUID, EstudianteDomain> {
}

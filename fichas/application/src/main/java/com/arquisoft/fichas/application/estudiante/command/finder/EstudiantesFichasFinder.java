package com.arquisoft.fichas.application.estudiante.command.finder;

import com.arquisoft.fichas.domain.estudiante.EstudianteDomain;
import com.arquisoft.shared.finder.Finder;

import java.util.List;
import java.util.UUID;

public interface EstudiantesFichasFinder extends Finder<List<UUID>, List<EstudianteDomain>> {
}

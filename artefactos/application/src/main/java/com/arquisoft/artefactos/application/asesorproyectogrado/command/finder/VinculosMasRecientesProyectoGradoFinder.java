package com.arquisoft.artefactos.application.asesorproyectogrado.command.finder;

import com.arquisoft.artefactos.domain.asesorproyectogrado.AsesorProyectoGradoDomain;
import com.arquisoft.shared.finder.Finder;

import java.util.List;
import java.util.UUID;

public interface VinculosMasRecientesProyectoGradoFinder extends Finder<UUID, List<AsesorProyectoGradoDomain>> {
}

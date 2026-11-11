package com.arquisoft.proyectos.application.asesor.command.finder;

import com.arquisoft.proyectos.domain.asesor.AsesorDomain;
import com.arquisoft.shared.finder.Finder;

import java.util.List;
import java.util.UUID;

public interface AsesoresAsignadosDeProyectoGradoFinder extends Finder<UUID, List<AsesorDomain>> {
}

package com.arquisoft.proyectos.application.asesor.command.finder;

import com.arquisoft.proyectos.domain.asesor.AsesorDomain;
import com.arquisoft.shared.finder.Finder;

import java.util.UUID;

public interface AsesorPorIdFinder extends Finder<UUID, AsesorDomain> {
}

package com.arquisoft.proyectos.application.coordinador.command.finder;

import com.arquisoft.proyectos.domain.coordinador.CoordinadorDomain;
import com.arquisoft.shared.finder.Finder;

import java.util.UUID;

public interface CoordinadorPorIdFinder extends Finder<UUID, CoordinadorDomain> {
}

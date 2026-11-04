package com.arquisoft.repositorio_artefactos.application.administrador.command.finder;

import com.arquisoft.repositorio_artefactos.domain.administrador.AdministradorDomain;
import com.arquisoft.shared.finder.Finder;

import java.util.UUID;

public interface AdministradorPorIdFinder extends Finder<UUID, AdministradorDomain> {
}

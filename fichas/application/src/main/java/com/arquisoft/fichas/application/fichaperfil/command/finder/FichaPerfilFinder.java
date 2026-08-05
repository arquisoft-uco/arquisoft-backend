package com.arquisoft.fichas.application.fichaperfil.command.finder;

import com.arquisoft.fichas.domain.fichaperfil.aggregate.FichaPerfilDomain;
import com.arquisoft.shared.rules.Finder;

import java.util.UUID;

public interface FichaPerfilFinder extends Finder<UUID, FichaPerfilDomain> {
}

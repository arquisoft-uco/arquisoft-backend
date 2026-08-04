package com.arquisoft.fichas.application.fichaperfil.command.finder;

import com.arquisoft.fichas.domain.fichaperfil.aggregate.FichaPerfilAggregate;
import com.arquisoft.shared.rules.Finder;

import java.util.UUID;

public interface FichaPerfilFinder extends Finder<UUID, FichaPerfilAggregate> {
}

package com.arquisoft.fichas.application.estadofichaperfil.command.finder;

import com.arquisoft.fichas.domain.estadofichaperfil.EstadoFichaPerfilDomain;
import com.arquisoft.shared.finder.Finder;

import java.util.Optional;
import java.util.UUID;

public interface EstadoActualFichaPerfilFinder extends Finder<UUID, Optional<EstadoFichaPerfilDomain>> {
}

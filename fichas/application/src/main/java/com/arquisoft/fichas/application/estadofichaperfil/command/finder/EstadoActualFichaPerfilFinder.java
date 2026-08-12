package com.arquisoft.fichas.application.estadofichaperfil.command.finder;

import com.arquisoft.fichas.domain.estadoficha.EstadoFicha;
import com.arquisoft.shared.rules.Finder;

import java.util.Optional;
import java.util.UUID;

public interface EstadoActualFichaPerfilFinder extends Finder<UUID, Optional<EstadoFicha>> {
}

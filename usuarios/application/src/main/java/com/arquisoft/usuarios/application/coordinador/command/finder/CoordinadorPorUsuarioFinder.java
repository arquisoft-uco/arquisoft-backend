package com.arquisoft.usuarios.application.coordinador.command.finder;

import com.arquisoft.shared.finder.Finder;
import com.arquisoft.usuarios.domain.coordinador.CoordinadorDomain;

import java.util.UUID;

public interface CoordinadorPorUsuarioFinder extends Finder<UUID, CoordinadorDomain> {
}

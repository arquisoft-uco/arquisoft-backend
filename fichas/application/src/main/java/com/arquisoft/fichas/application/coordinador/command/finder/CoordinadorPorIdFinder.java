package com.arquisoft.fichas.application.coordinador.command.finder;

import com.arquisoft.fichas.application.coordinador.command.secondaryport.entity.CoordinadorEntity;
import com.arquisoft.shared.finder.Finder;

import java.util.Optional;
import java.util.UUID;

public interface CoordinadorPorIdFinder extends Finder<UUID, Optional<CoordinadorEntity>> {
}

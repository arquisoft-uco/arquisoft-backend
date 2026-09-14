package com.arquisoft.fichas.application.asesorficha.command.finder;

import com.arquisoft.fichas.application.asesorficha.command.secondaryport.entity.AsesorFichaEntity;
import com.arquisoft.shared.finder.Finder;

import java.util.Optional;
import java.util.UUID;

public interface AsesorFichaPorIdFinder extends Finder<UUID, Optional<AsesorFichaEntity>> {
}

package com.arquisoft.fichas.application.asesorficha.command.finder;

import com.arquisoft.fichas.domain.asesorficha.AsesorFichaDomain;
import com.arquisoft.shared.finder.Finder;

import java.util.Optional;
import java.util.UUID;

public interface AsesorFichaFichasFinder extends Finder<UUID, Optional<AsesorFichaDomain>> {
}

package com.arquisoft.fichas.application.asesorficha.command.finder;

import com.arquisoft.fichas.domain.asesorficha.model.ContactoAsesorFicha;
import com.arquisoft.shared.rules.Finder;

import java.util.Optional;
import java.util.UUID;

public interface AsesorFichaFinder extends Finder<UUID, Optional<ContactoAsesorFicha>> {
}

package com.arquisoft.fichas.application.estadoevaluacionficha.command.finder;

import com.arquisoft.fichas.domain.estadoevaluacionficha.EstadoEvaluacionFichaDomain;
import com.arquisoft.shared.finder.Finder;

import java.util.UUID;

public interface UltimoEstadoEvaluacionFichaFinder
        extends Finder<UUID, EstadoEvaluacionFichaDomain> {
}

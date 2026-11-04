package com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.finder;

import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.OmisionEvaluacionesCualitativasJuradoDomain;
import com.arquisoft.shared.finder.Finder;

import java.util.Set;
import java.util.UUID;

public interface EvaluacionesCualitativasJuradoDeEvaluacionFinder
        extends Finder<OmisionEvaluacionesCualitativasJuradoDomain, Set<UUID>> {
}

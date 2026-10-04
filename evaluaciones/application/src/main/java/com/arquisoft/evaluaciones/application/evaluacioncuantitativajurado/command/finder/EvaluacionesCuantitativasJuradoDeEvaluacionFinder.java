package com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.finder;

import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.OmisionEvaluacionesCuantitativasJuradoDomain;
import com.arquisoft.shared.finder.Finder;

import java.util.Set;
import java.util.UUID;

public interface EvaluacionesCuantitativasJuradoDeEvaluacionFinder
        extends Finder<OmisionEvaluacionesCuantitativasJuradoDomain, Set<UUID>> {
}

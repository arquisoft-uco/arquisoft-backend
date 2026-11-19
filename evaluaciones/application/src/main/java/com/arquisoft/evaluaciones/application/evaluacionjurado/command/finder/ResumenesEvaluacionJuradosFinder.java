package com.arquisoft.evaluaciones.application.evaluacionjurado.command.finder;

import com.arquisoft.evaluaciones.domain.evaluacion.ResumenEvaluacionDomain;
import com.arquisoft.shared.finder.Finder;

import java.util.List;
import java.util.UUID;

public interface ResumenesEvaluacionJuradosFinder extends Finder<UUID, List<ResumenEvaluacionDomain>> {
}

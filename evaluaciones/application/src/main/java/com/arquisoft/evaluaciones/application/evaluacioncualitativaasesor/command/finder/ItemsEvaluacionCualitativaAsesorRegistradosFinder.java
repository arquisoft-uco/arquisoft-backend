package com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.finder;

import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.finder.model.ItemsPorEvaluacionAsesor;
import com.arquisoft.shared.finder.Finder;

import java.util.Set;
import java.util.UUID;

public interface ItemsEvaluacionCualitativaAsesorRegistradosFinder
        extends Finder<ItemsPorEvaluacionAsesor, Set<UUID>> {
}

package com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.finder;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.finder.model.ItemsPorEvaluacionCuantitativaAsesor;
import com.arquisoft.shared.finder.Finder;

import java.util.Set;
import java.util.UUID;

public interface ItemsEvaluacionCuantitativaAsesorRegistradosFinder
        extends Finder<ItemsPorEvaluacionCuantitativaAsesor, Set<UUID>> {
}

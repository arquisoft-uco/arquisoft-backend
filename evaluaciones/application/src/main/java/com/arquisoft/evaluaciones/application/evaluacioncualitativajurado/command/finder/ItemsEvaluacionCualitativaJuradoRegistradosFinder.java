package com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.finder;

import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.finder.model.CriterioItemsEvaluacion;
import com.arquisoft.shared.finder.Finder;

import java.util.Set;
import java.util.UUID;

public interface ItemsEvaluacionCualitativaJuradoRegistradosFinder
        extends Finder<CriterioItemsEvaluacion, Set<UUID>> {
}

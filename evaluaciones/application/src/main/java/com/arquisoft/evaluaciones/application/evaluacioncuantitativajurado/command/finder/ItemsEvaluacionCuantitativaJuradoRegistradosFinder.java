package com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.finder;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.finder.model.ItemsPorEvaluacionCuantitativaJurado;
import com.arquisoft.shared.finder.Finder;

import java.util.Set;
import java.util.UUID;

public interface ItemsEvaluacionCuantitativaJuradoRegistradosFinder
        extends Finder<ItemsPorEvaluacionCuantitativaJurado, Set<UUID>> {
}

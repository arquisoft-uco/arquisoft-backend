package com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.finder;

import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.EvaluacionCuantitativaJuradoDomain;
import com.arquisoft.shared.finder.Finder;

import java.util.Optional;
import java.util.UUID;

public interface EvaluacionCuantitativaJuradoPorIdFinder extends Finder<UUID, Optional<EvaluacionCuantitativaJuradoDomain>> {
}

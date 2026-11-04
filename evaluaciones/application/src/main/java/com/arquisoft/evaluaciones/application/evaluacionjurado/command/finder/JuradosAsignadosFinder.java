package com.arquisoft.evaluaciones.application.evaluacionjurado.command.finder;

import com.arquisoft.evaluaciones.application.evaluacionjurado.command.finder.model.CriterioJuradosEvaluacion;
import com.arquisoft.shared.finder.Finder;

import java.util.Set;
import java.util.UUID;

public interface JuradosAsignadosFinder extends Finder<CriterioJuradosEvaluacion, Set<UUID>> {
}

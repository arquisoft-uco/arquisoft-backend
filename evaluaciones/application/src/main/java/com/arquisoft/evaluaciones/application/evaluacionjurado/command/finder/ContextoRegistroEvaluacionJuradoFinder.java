package com.arquisoft.evaluaciones.application.evaluacionjurado.command.finder;

import com.arquisoft.evaluaciones.application.evaluacionjurado.command.secondaryport.entity.ContextoRegistroEvaluacionJuradoEntity;
import com.arquisoft.shared.finder.Finder;

import java.util.Optional;
import java.util.UUID;

public interface ContextoRegistroEvaluacionJuradoFinder
        extends Finder<UUID, Optional<ContextoRegistroEvaluacionJuradoEntity>> {
}

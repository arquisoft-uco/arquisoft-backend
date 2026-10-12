package com.arquisoft.fichas.application.estadoficha.query.usecase;

import com.arquisoft.fichas.application.estadoficha.query.criteria.EstadoFichaCriteria;
import com.arquisoft.fichas.application.estadoficha.query.readmodel.EstadoFichaReadModel;
import com.arquisoft.shared.usecase.UseCase;

import java.util.List;

public interface ConsultarEstadosFichaUseCase extends UseCase<EstadoFichaCriteria, List<EstadoFichaReadModel>> {
}

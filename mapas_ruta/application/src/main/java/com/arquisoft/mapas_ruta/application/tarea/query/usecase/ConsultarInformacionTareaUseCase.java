package com.arquisoft.mapas_ruta.application.tarea.query.usecase;

import com.arquisoft.mapas_ruta.application.tarea.query.criteria.TareaCriteria;
import com.arquisoft.mapas_ruta.application.tarea.query.readmodel.TareaReadModel;
import com.arquisoft.shared.usecase.UseCase;

import java.util.Optional;

public interface ConsultarInformacionTareaUseCase extends UseCase<TareaCriteria, Optional<TareaReadModel>> {
}

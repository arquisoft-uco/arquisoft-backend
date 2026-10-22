package com.arquisoft.mapas_ruta.application.tarea.query.primaryport.interactor;

import com.arquisoft.mapas_ruta.application.tarea.query.primaryport.model.ConsultarInformacionTareaQuery;
import com.arquisoft.mapas_ruta.application.tarea.query.readmodel.TareaReadModel;
import com.arquisoft.shared.interactor.Interactor;

import java.util.Optional;

public interface ConsultarInformacionTareaInteractor
        extends Interactor<ConsultarInformacionTareaQuery, Optional<TareaReadModel>> {
}

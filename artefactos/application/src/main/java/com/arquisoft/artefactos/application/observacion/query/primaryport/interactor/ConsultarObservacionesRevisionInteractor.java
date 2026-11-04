package com.arquisoft.artefactos.application.observacion.query.primaryport.interactor;

import com.arquisoft.artefactos.application.observacion.query.primaryport.model.ConsultarObservacionesRevisionQuery;
import com.arquisoft.artefactos.application.observacion.query.readmodel.ObservacionReadModel;
import com.arquisoft.shared.interactor.Interactor;

import java.util.List;

public interface ConsultarObservacionesRevisionInteractor
        extends Interactor<ConsultarObservacionesRevisionQuery, List<ObservacionReadModel>> {}

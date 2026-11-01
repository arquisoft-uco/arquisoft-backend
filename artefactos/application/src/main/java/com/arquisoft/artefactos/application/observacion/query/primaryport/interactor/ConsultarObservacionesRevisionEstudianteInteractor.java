package com.arquisoft.artefactos.application.observacion.query.primaryport.interactor;

import com.arquisoft.artefactos.application.observacion.query.primaryport.model.ConsultarObservacionesRevisionEstudianteQuery;
import com.arquisoft.artefactos.application.observacion.query.readmodel.ObservacionReadModel;
import com.arquisoft.shared.interactor.Interactor;

import java.util.List;

public interface ConsultarObservacionesRevisionEstudianteInteractor
        extends Interactor<ConsultarObservacionesRevisionEstudianteQuery, List<ObservacionReadModel>> {}

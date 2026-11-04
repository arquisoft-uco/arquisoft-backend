package com.arquisoft.artefactos.application.observacion.query.usecase;

import com.arquisoft.artefactos.application.observacion.query.criteria.ObservacionEstudianteCriteria;
import com.arquisoft.artefactos.application.observacion.query.readmodel.ObservacionReadModel;
import com.arquisoft.shared.usecase.UseCase;

import java.util.List;

public interface ConsultarObservacionesRevisionEstudianteUseCase
        extends UseCase<ObservacionEstudianteCriteria, List<ObservacionReadModel>> {}

package com.arquisoft.fichas.application.evaluacionfichaperfil.query.usecase;

import com.arquisoft.fichas.application.evaluacionfichaperfil.query.criteria.EvaluacionFichaPerfilEstudianteCriteria;
import com.arquisoft.fichas.application.evaluacionfichaperfil.query.readmodel.EvaluacionFichaPerfilEstudianteReadModel;
import com.arquisoft.shared.usecase.UseCase;

import java.util.List;

public interface ConsultarEvaluacionesFichaPerfilEstudianteUseCase
        extends UseCase<EvaluacionFichaPerfilEstudianteCriteria, List<EvaluacionFichaPerfilEstudianteReadModel>> {}

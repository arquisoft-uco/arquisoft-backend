package com.arquisoft.fichas.application.evaluacionfichaperfil.query.usecase;

import com.arquisoft.fichas.application.evaluacionfichaperfil.query.criteria.EvaluacionFichaPerfilCoordinadorCriteria;
import com.arquisoft.fichas.application.evaluacionfichaperfil.query.readmodel.EvaluacionFichaPerfilCoordinadorReadModel;
import com.arquisoft.shared.usecase.UseCase;

import java.util.List;

public interface ConsultarEvaluacionesFichaPerfilCoordinadorUseCase
        extends UseCase<EvaluacionFichaPerfilCoordinadorCriteria, List<EvaluacionFichaPerfilCoordinadorReadModel>> {}

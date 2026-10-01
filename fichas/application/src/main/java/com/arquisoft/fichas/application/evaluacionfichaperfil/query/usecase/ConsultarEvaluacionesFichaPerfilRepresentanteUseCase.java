package com.arquisoft.fichas.application.evaluacionfichaperfil.query.usecase;

import com.arquisoft.fichas.application.evaluacionfichaperfil.query.criteria.EvaluacionFichaPerfilRepresentanteCriteria;
import com.arquisoft.fichas.application.evaluacionfichaperfil.query.readmodel.EvaluacionFichaPerfilReadModel;
import com.arquisoft.shared.usecase.UseCase;

import java.util.List;

public interface ConsultarEvaluacionesFichaPerfilRepresentanteUseCase
        extends UseCase<EvaluacionFichaPerfilRepresentanteCriteria, List<EvaluacionFichaPerfilReadModel>> {}

package com.arquisoft.fichas.application.fichaperfil.query.secondaryport;

import com.arquisoft.fichas.application.fichaperfil.query.criteria.FichaPerfilDisponibleEvaluacionCriteria;
import com.arquisoft.fichas.application.fichaperfil.query.readmodel.FichaPerfilReadModel;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface FichaPerfilDisponibleEvaluacionQueryOutputPort {

    PaginatedResult<FichaPerfilReadModel> consultarDisponiblesParaEvaluar(
            FichaPerfilDisponibleEvaluacionCriteria criteria);
}

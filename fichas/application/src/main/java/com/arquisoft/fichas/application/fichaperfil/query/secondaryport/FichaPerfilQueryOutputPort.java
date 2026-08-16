package com.arquisoft.fichas.application.fichaperfil.query.secondaryport;

import com.arquisoft.fichas.application.fichaperfil.query.criteria.FichaPerfilCriteria;
import com.arquisoft.fichas.application.fichaperfil.query.readmodel.FichaPerfilReadModel;
import com.arquisoft.shared.query.pagination.PaginatedResult;

import java.util.UUID;

public interface FichaPerfilQueryOutputPort {

    PaginatedResult<FichaPerfilReadModel> consultarTodas(FichaPerfilCriteria criteria);

    boolean existePorId(UUID id);
}

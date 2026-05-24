package com.arquisoft.fichas.application.fichaperfil.query.port.out;

import com.arquisoft.fichas.application.fichaperfil.query.readmodel.FichaPerfilReadModel;
import com.arquisoft.shared.pagination.PaginatedResult;
import com.arquisoft.shared.pagination.PaginationRequest;

public interface FichaPerfilQueryOutputPort {

    PaginatedResult<FichaPerfilReadModel> consultarTodas(PaginationRequest request);
}

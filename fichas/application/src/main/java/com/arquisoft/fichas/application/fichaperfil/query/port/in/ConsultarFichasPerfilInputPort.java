package com.arquisoft.fichas.application.fichaperfil.query.port.in;

import com.arquisoft.fichas.application.fichaperfil.query.readmodel.FichaPerfilReadModel;
import com.arquisoft.shared.pagination.PaginatedResult;
import com.arquisoft.shared.pagination.PaginationRequest;

public interface ConsultarFichasPerfilInputPort {

    PaginatedResult<FichaPerfilReadModel> ejecutar(PaginationRequest request);
}

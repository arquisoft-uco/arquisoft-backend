package com.arquisoft.fichas.application.fichaperfil.query.port.in;

import com.arquisoft.fichas.application.fichaperfil.query.criteria.FichaPerfilCriteria;
import com.arquisoft.fichas.application.fichaperfil.query.readmodel.FichaPerfilReadModel;
import com.arquisoft.shared.pagination.PaginatedResult;

public interface ConsultarFichasPerfilInputPort {

    PaginatedResult<FichaPerfilReadModel> ejecutar(FichaPerfilCriteria criteria);
}

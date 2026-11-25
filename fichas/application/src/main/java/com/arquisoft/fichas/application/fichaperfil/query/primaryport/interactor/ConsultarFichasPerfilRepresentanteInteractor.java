package com.arquisoft.fichas.application.fichaperfil.query.primaryport.interactor;

import com.arquisoft.fichas.application.fichaperfil.query.primaryport.model.ConsultarFichasPerfilRepresentanteQuery;
import com.arquisoft.fichas.application.fichaperfil.query.readmodel.FichaPerfilRepresentanteReadModel;
import com.arquisoft.shared.interactor.Interactor;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface ConsultarFichasPerfilRepresentanteInteractor
        extends Interactor<ConsultarFichasPerfilRepresentanteQuery, PaginatedResult<FichaPerfilRepresentanteReadModel>> {}

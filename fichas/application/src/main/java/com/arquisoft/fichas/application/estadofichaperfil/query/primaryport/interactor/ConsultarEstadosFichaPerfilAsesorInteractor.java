package com.arquisoft.fichas.application.estadofichaperfil.query.primaryport.interactor;

import com.arquisoft.fichas.application.estadofichaperfil.query.primaryport.model.ConsultarEstadosFichaPerfilAsesorQuery;
import com.arquisoft.fichas.application.estadofichaperfil.query.readmodel.EstadoFichaPerfilAsesorReadModel;
import com.arquisoft.shared.interactor.Interactor;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface ConsultarEstadosFichaPerfilAsesorInteractor
        extends Interactor<ConsultarEstadosFichaPerfilAsesorQuery, PaginatedResult<EstadoFichaPerfilAsesorReadModel>> {}

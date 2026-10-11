package com.arquisoft.fichas.application.estadofichaperfil.query.primaryport.interactor;

import com.arquisoft.fichas.application.estadofichaperfil.query.primaryport.model.ConsultarEstadosFichaPerfilCoordinadorQuery;
import com.arquisoft.fichas.application.estadofichaperfil.query.readmodel.EstadoFichaPerfilReadModel;
import com.arquisoft.shared.interactor.Interactor;

import java.util.List;

public interface ConsultarEstadosFichaPerfilCoordinadorInteractor
        extends Interactor<ConsultarEstadosFichaPerfilCoordinadorQuery, List<EstadoFichaPerfilReadModel>> {}

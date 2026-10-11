package com.arquisoft.fichas.application.itemfichaperfil.query.primaryport.interactor;

import com.arquisoft.fichas.application.itemfichaperfil.query.primaryport.model.ConsultarItemsFichaPerfilCoordinadorQuery;
import com.arquisoft.fichas.application.itemfichaperfil.query.readmodel.ItemFichaPerfilReadModel;
import com.arquisoft.shared.interactor.Interactor;

import java.util.List;

public interface ConsultarItemsFichaPerfilCoordinadorInteractor
        extends Interactor<ConsultarItemsFichaPerfilCoordinadorQuery, List<ItemFichaPerfilReadModel>> {}

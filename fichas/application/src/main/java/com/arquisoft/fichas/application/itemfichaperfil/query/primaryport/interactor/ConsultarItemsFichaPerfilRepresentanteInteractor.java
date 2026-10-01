package com.arquisoft.fichas.application.itemfichaperfil.query.primaryport.interactor;

import com.arquisoft.fichas.application.itemfichaperfil.query.primaryport.model.ConsultarItemsFichaPerfilRepresentanteQuery;
import com.arquisoft.fichas.application.itemfichaperfil.query.readmodel.ItemFichaPerfilReadModel;
import com.arquisoft.shared.interactor.Interactor;

import java.util.List;

public interface ConsultarItemsFichaPerfilRepresentanteInteractor
        extends Interactor<ConsultarItemsFichaPerfilRepresentanteQuery, List<ItemFichaPerfilReadModel>> {}

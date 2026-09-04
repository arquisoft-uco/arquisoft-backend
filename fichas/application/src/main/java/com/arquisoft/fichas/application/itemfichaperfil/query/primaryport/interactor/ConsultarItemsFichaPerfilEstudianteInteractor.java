package com.arquisoft.fichas.application.itemfichaperfil.query.primaryport.interactor;

import com.arquisoft.fichas.application.itemfichaperfil.query.primaryport.model.ConsultarItemsFichaPerfilEstudianteQuery;
import com.arquisoft.fichas.application.itemfichaperfil.query.readmodel.ItemFichaPerfilReadModel;
import com.arquisoft.shared.interactor.Interactor;

import java.util.List;

public interface ConsultarItemsFichaPerfilEstudianteInteractor
        extends Interactor<ConsultarItemsFichaPerfilEstudianteQuery, List<ItemFichaPerfilReadModel>> {}

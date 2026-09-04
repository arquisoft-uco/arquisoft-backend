package com.arquisoft.fichas.application.itemfichaperfil.query.usecase;

import com.arquisoft.fichas.application.itemfichaperfil.query.criteria.ItemFichaPerfilEstudianteCriteria;
import com.arquisoft.fichas.application.itemfichaperfil.query.readmodel.ItemFichaPerfilReadModel;
import com.arquisoft.shared.usecase.UseCase;

import java.util.List;

public interface ConsultarItemsFichaPerfilEstudianteUseCase
        extends UseCase<ItemFichaPerfilEstudianteCriteria, List<ItemFichaPerfilReadModel>> {}

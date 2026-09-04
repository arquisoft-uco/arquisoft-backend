package com.arquisoft.fichas.application.itemfichaperfil.query.usecase;

import com.arquisoft.fichas.application.itemfichaperfil.query.criteria.ItemFichaPerfilCriteria;
import com.arquisoft.fichas.application.itemfichaperfil.query.readmodel.ItemFichaPerfilReadModel;
import com.arquisoft.shared.usecase.UseCase;

import java.util.List;

public interface ConsultarItemsFichaPerfilAsesorUseCase
        extends UseCase<ItemFichaPerfilCriteria, List<ItemFichaPerfilReadModel>> {}

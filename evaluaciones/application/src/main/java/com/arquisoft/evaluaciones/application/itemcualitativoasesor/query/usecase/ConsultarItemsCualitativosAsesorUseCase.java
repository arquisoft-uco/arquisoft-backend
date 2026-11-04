package com.arquisoft.evaluaciones.application.itemcualitativoasesor.query.usecase;

import com.arquisoft.evaluaciones.application.itemcualitativoasesor.query.readmodel.ItemCualitativoAsesorReadModel;
import com.arquisoft.shared.usecase.SupplierUseCase;

import java.util.List;

public interface ConsultarItemsCualitativosAsesorUseCase
        extends SupplierUseCase<List<ItemCualitativoAsesorReadModel>> {
}

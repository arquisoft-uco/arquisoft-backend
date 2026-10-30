package com.arquisoft.evaluaciones.application.itemcuantitativoasesor.query.usecase;

import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.query.readmodel.ItemCuantitativoAsesorReadModel;
import com.arquisoft.shared.usecase.SupplierUseCase;

import java.util.List;

public interface ConsultarItemsCuantitativosAsesorUseCase
        extends SupplierUseCase<List<ItemCuantitativoAsesorReadModel>> {
}

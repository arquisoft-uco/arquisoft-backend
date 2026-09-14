package com.arquisoft.evaluaciones.application.itemcuantitativojurado.query.usecase;

import com.arquisoft.evaluaciones.application.itemcuantitativojurado.query.readmodel.ItemCuantitativoJuradoReadModel;
import com.arquisoft.shared.usecase.SupplierUseCase;

import java.util.List;

public interface ConsultarItemsCuantitativosJuradoUseCase
        extends SupplierUseCase<List<ItemCuantitativoJuradoReadModel>> {
}

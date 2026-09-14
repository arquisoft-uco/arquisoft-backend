package com.arquisoft.evaluaciones.application.categoriaitemcuantitativojurado.query.usecase;

import com.arquisoft.evaluaciones.application.categoriaitemcuantitativojurado.query.readmodel.CategoriaItemCuantitativoJuradoReadModel;
import com.arquisoft.shared.usecase.UseCase;

import java.util.List;

public interface ConsultarCategoriasItemCuantitativoJuradoUseCase
        extends UseCase<String, List<CategoriaItemCuantitativoJuradoReadModel>> {
}

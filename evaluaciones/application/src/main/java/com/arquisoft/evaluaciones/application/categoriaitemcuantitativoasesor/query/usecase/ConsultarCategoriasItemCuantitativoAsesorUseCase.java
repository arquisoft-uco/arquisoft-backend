package com.arquisoft.evaluaciones.application.categoriaitemcuantitativoasesor.query.usecase;

import com.arquisoft.evaluaciones.application.categoriaitemcuantitativoasesor.query.readmodel.CategoriaItemCuantitativoAsesorReadModel;
import com.arquisoft.shared.usecase.UseCase;

import java.util.List;

public interface ConsultarCategoriasItemCuantitativoAsesorUseCase
        extends UseCase<String, List<CategoriaItemCuantitativoAsesorReadModel>> {
}

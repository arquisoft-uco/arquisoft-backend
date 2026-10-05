package com.arquisoft.evaluaciones.application.categoriaitemcuantitativoasesor.query.primaryport.interactor;

import com.arquisoft.evaluaciones.application.categoriaitemcuantitativoasesor.query.primaryport.model.ConsultarCategoriasItemCuantitativoAsesorQuery;
import com.arquisoft.evaluaciones.application.categoriaitemcuantitativoasesor.query.readmodel.CategoriaItemCuantitativoAsesorReadModel;
import com.arquisoft.shared.interactor.Interactor;

import java.util.List;

public interface ConsultarCategoriasItemCuantitativoAsesorInteractor
        extends Interactor<ConsultarCategoriasItemCuantitativoAsesorQuery, List<CategoriaItemCuantitativoAsesorReadModel>> {
}

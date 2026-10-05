package com.arquisoft.evaluaciones.application.categoriaitemcuantitativoasesor.query.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.categoriaitemcuantitativoasesor.query.primaryport.interactor.ConsultarCategoriasItemCuantitativoAsesorInteractor;
import com.arquisoft.evaluaciones.application.categoriaitemcuantitativoasesor.query.primaryport.model.ConsultarCategoriasItemCuantitativoAsesorQuery;
import com.arquisoft.evaluaciones.application.categoriaitemcuantitativoasesor.query.readmodel.CategoriaItemCuantitativoAsesorReadModel;
import com.arquisoft.evaluaciones.application.categoriaitemcuantitativoasesor.query.usecase.ConsultarCategoriasItemCuantitativoAsesorUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarCategoriasItemCuantitativoAsesorInteractorImpl
        implements ConsultarCategoriasItemCuantitativoAsesorInteractor {

    private final ConsultarCategoriasItemCuantitativoAsesorUseCase consultarCategoriasItemCuantitativoAsesorUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "evaluacionesTransactionManager")
    public List<CategoriaItemCuantitativoAsesorReadModel> ejecutar(
            ConsultarCategoriasItemCuantitativoAsesorQuery query) {
        return consultarCategoriasItemCuantitativoAsesorUseCase.ejecutar(query.nombre());
    }
}

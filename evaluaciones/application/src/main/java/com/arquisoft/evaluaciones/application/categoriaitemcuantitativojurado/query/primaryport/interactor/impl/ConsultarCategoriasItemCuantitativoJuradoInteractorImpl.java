package com.arquisoft.evaluaciones.application.categoriaitemcuantitativojurado.query.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.categoriaitemcuantitativojurado.query.primaryport.interactor.ConsultarCategoriasItemCuantitativoJuradoInteractor;
import com.arquisoft.evaluaciones.application.categoriaitemcuantitativojurado.query.primaryport.model.ConsultarCategoriasItemCuantitativoJuradoQuery;
import com.arquisoft.evaluaciones.application.categoriaitemcuantitativojurado.query.readmodel.CategoriaItemCuantitativoJuradoReadModel;
import com.arquisoft.evaluaciones.application.categoriaitemcuantitativojurado.query.usecase.ConsultarCategoriasItemCuantitativoJuradoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarCategoriasItemCuantitativoJuradoInteractorImpl
        implements ConsultarCategoriasItemCuantitativoJuradoInteractor {

    private final ConsultarCategoriasItemCuantitativoJuradoUseCase consultarCategoriasItemCuantitativoJuradoUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "evaluacionesTransactionManager")
    public List<CategoriaItemCuantitativoJuradoReadModel> ejecutar(
            ConsultarCategoriasItemCuantitativoJuradoQuery query) {
        return consultarCategoriasItemCuantitativoJuradoUseCase.ejecutar(query.nombre());
    }
}

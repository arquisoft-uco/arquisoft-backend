package com.arquisoft.evaluaciones.application.categoriaitemcuantitativojurado.query.usecase.impl;

import com.arquisoft.evaluaciones.application.categoriaitemcuantitativojurado.query.readmodel.CategoriaItemCuantitativoJuradoReadModel;
import com.arquisoft.evaluaciones.application.categoriaitemcuantitativojurado.query.secondaryport.CategoriaItemCuantitativoJuradoQueryOutputPort;
import com.arquisoft.evaluaciones.application.categoriaitemcuantitativojurado.query.usecase.ConsultarCategoriasItemCuantitativoJuradoUseCase;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.CategoriaItemCuantitativoJuradoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarCategoriasItemCuantitativoJuradoUseCaseImpl
        implements ConsultarCategoriasItemCuantitativoJuradoUseCase {

    private final CategoriaItemCuantitativoJuradoQueryOutputPort queryOutputPort;
    private final AppLogger logger;

    @Override
    public List<CategoriaItemCuantitativoJuradoReadModel> ejecutar(String nombre) {
        logger.debug(CategoriaItemCuantitativoJuradoKey.LOG_CONSULTANDO, nombre != null);

        var resultado = queryOutputPort.consultar(nombre);

        logger.debug(CategoriaItemCuantitativoJuradoKey.LOG_CONSULTA_COMPLETADA, resultado.size());

        return resultado;
    }
}

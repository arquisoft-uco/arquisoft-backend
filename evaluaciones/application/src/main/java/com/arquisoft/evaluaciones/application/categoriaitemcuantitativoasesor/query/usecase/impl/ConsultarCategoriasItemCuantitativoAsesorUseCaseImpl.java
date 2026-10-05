package com.arquisoft.evaluaciones.application.categoriaitemcuantitativoasesor.query.usecase.impl;

import com.arquisoft.evaluaciones.application.categoriaitemcuantitativoasesor.query.readmodel.CategoriaItemCuantitativoAsesorReadModel;
import com.arquisoft.evaluaciones.application.categoriaitemcuantitativoasesor.query.secondaryport.CategoriaItemCuantitativoAsesorQueryOutputPort;
import com.arquisoft.evaluaciones.application.categoriaitemcuantitativoasesor.query.usecase.ConsultarCategoriasItemCuantitativoAsesorUseCase;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.CategoriaItemCuantitativoAsesorKey;
import com.arquisoft.shared.util.UtilObjeto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarCategoriasItemCuantitativoAsesorUseCaseImpl
        implements ConsultarCategoriasItemCuantitativoAsesorUseCase {

    private final CategoriaItemCuantitativoAsesorQueryOutputPort queryOutputPort;
    private final AppLogger logger;

    @Override
    public List<CategoriaItemCuantitativoAsesorReadModel> ejecutar(String nombre) {
        logger.debug(CategoriaItemCuantitativoAsesorKey.LOG_CONSULTANDO, UtilObjeto.noEsNulo(nombre));

        var resultado = queryOutputPort.consultar(nombre);

        logger.debug(CategoriaItemCuantitativoAsesorKey.LOG_CONSULTA_COMPLETADA, resultado.size());

        return resultado;
    }
}

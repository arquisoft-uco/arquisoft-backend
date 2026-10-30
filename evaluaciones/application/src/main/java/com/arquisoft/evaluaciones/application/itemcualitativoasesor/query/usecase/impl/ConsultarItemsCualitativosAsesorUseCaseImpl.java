package com.arquisoft.evaluaciones.application.itemcualitativoasesor.query.usecase.impl;

import com.arquisoft.evaluaciones.application.itemcualitativoasesor.query.readmodel.ItemCualitativoAsesorReadModel;
import com.arquisoft.evaluaciones.application.itemcualitativoasesor.query.secondaryport.ItemCualitativoAsesorQueryOutputPort;
import com.arquisoft.evaluaciones.application.itemcualitativoasesor.query.usecase.ConsultarItemsCualitativosAsesorUseCase;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.ItemCualitativoAsesorKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarItemsCualitativosAsesorUseCaseImpl implements ConsultarItemsCualitativosAsesorUseCase {

    private final ItemCualitativoAsesorQueryOutputPort queryOutputPort;
    private final AppLogger logger;

    @Override
    public List<ItemCualitativoAsesorReadModel> ejecutar() {
        var resultado = queryOutputPort.consultarTodos();

        logger.debug(ItemCualitativoAsesorKey.LOG_CONSULTA_COMPLETADA, resultado.size());

        return resultado;
    }
}

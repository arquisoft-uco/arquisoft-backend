package com.arquisoft.evaluaciones.application.itemcuantitativoasesor.query.usecase.impl;

import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.query.readmodel.ItemCuantitativoAsesorReadModel;
import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.query.secondaryport.ItemCuantitativoAsesorQueryOutputPort;
import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.query.usecase.ConsultarItemsCuantitativosAsesorUseCase;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.ItemCuantitativoAsesorKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarItemsCuantitativosAsesorUseCaseImpl implements ConsultarItemsCuantitativosAsesorUseCase {

    private final ItemCuantitativoAsesorQueryOutputPort queryOutputPort;
    private final AppLogger logger;

    @Override
    public List<ItemCuantitativoAsesorReadModel> ejecutar() {
        var resultado = queryOutputPort.consultarTodos();

        logger.debug(ItemCuantitativoAsesorKey.LOG_CONSULTA_COMPLETADA, resultado.size());

        return resultado;
    }
}

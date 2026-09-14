package com.arquisoft.evaluaciones.application.itemcuantitativojurado.query.usecase.impl;

import com.arquisoft.evaluaciones.application.itemcuantitativojurado.query.readmodel.ItemCuantitativoJuradoReadModel;
import com.arquisoft.evaluaciones.application.itemcuantitativojurado.query.secondaryport.ItemCuantitativoJuradoQueryOutputPort;
import com.arquisoft.evaluaciones.application.itemcuantitativojurado.query.usecase.ConsultarItemsCuantitativosJuradoUseCase;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.ItemCuantitativoJuradoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarItemsCuantitativosJuradoUseCaseImpl implements ConsultarItemsCuantitativosJuradoUseCase {

    private final ItemCuantitativoJuradoQueryOutputPort queryOutputPort;
    private final AppLogger logger;

    @Override
    public List<ItemCuantitativoJuradoReadModel> ejecutar() {
        var resultado = queryOutputPort.consultarTodos();

        logger.debug(ItemCuantitativoJuradoKey.LOG_CONSULTA_COMPLETADA, resultado.size());

        return resultado;
    }
}

package com.arquisoft.evaluaciones.application.itemcualitativojurado.query.usecase.impl;

import com.arquisoft.evaluaciones.application.itemcualitativojurado.query.readmodel.ItemCualitativoJuradoReadModel;
import com.arquisoft.evaluaciones.application.itemcualitativojurado.query.secondaryport.ItemCualitativoJuradoQueryOutputPort;
import com.arquisoft.evaluaciones.application.itemcualitativojurado.query.usecase.ConsultarItemsCualitativosJuradoUseCase;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.key.evaluaciones.ItemCualitativoJuradoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarItemsCualitativosJuradoUseCaseImpl implements ConsultarItemsCualitativosJuradoUseCase {

    private final ItemCualitativoJuradoQueryOutputPort queryOutputPort;
    private final AppLogger logger;

    @Override
    public List<ItemCualitativoJuradoReadModel> ejecutar() {
        var resultado = queryOutputPort.consultarTodos();

        logger.debug(Mensajes.obtener(ItemCualitativoJuradoKey.LOG_CONSULTA_COMPLETADA), resultado.size());

        return resultado;
    }
}

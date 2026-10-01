package com.arquisoft.fichas.application.tipoitem.query.usecase.impl;

import com.arquisoft.fichas.application.tipoitem.query.usecase.ConsultarTiposItemUseCase;
import com.arquisoft.fichas.application.tipoitem.query.secondaryport.TipoItemQueryOutputPort;
import com.arquisoft.fichas.application.tipoitem.query.readmodel.TipoItemReadModel;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.TipoItemKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarTiposItemUseCaseImpl implements ConsultarTiposItemUseCase {

    private final TipoItemQueryOutputPort queryOutputPort;
    private final AppLogger logger;

    @Override
    public List<TipoItemReadModel> ejecutar() {
        var resultado = queryOutputPort.consultarTodos();

        logger.debug(TipoItemKey.LOG_CONSULTA_COMPLETADA, resultado.size());

        return resultado;
    }
}

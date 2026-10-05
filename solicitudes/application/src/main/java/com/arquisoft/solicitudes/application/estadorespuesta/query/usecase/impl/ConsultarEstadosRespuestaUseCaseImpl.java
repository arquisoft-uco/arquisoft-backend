package com.arquisoft.solicitudes.application.estadorespuesta.query.usecase.impl;

import com.arquisoft.solicitudes.application.estadorespuesta.query.readmodel.EstadoRespuestaReadModel;
import com.arquisoft.solicitudes.application.estadorespuesta.query.secondaryport.EstadoRespuestaQueryOutputPort;
import com.arquisoft.solicitudes.application.estadorespuesta.query.usecase.ConsultarEstadosRespuestaUseCase;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.solicitudes.EstadoRespuestaKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarEstadosRespuestaUseCaseImpl implements ConsultarEstadosRespuestaUseCase {

    private final EstadoRespuestaQueryOutputPort queryOutputPort;
    private final AppLogger logger;

    @Override
    public List<EstadoRespuestaReadModel> ejecutar() {
        var resultado = queryOutputPort.findAll();

        logger.debug(EstadoRespuestaKey.LOG_CONSULTA_COMPLETADA, resultado.size());

        return resultado;
    }
}

package com.arquisoft.solicitudes.application.tiposolicitud.query.usecase.impl;

import com.arquisoft.solicitudes.application.tiposolicitud.query.readmodel.TipoSolicitudReadModel;
import com.arquisoft.solicitudes.application.tiposolicitud.query.secondaryport.TipoSolicitudQueryOutputPort;
import com.arquisoft.solicitudes.application.tiposolicitud.query.usecase.ConsultarTiposSolicitudUseCase;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.solicitudes.TipoSolicitudKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarTiposSolicitudUseCaseImpl implements ConsultarTiposSolicitudUseCase {

    private final TipoSolicitudQueryOutputPort queryOutputPort;
    private final AppLogger logger;

    @Override
    public List<TipoSolicitudReadModel> ejecutar() {
        var resultado = queryOutputPort.findAll();

        logger.debug(TipoSolicitudKey.LOG_CONSULTA_COMPLETADA, resultado.size());

        return resultado;
    }
}

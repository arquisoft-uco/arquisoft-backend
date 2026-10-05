package com.arquisoft.solicitudes.application.tiposolicitud.query.usecase.impl;

import com.arquisoft.solicitudes.application.tiposolicitud.query.criteria.TipoSolicitudCriteria;
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
    public List<TipoSolicitudReadModel> ejecutar(TipoSolicitudCriteria entrada) {
        logger.debug(TipoSolicitudKey.LOG_CONSULTANDO, entrada.tipos().size());

        var resultado = entrada.tipos().isEmpty()
                ? List.<TipoSolicitudReadModel>of()
                : queryOutputPort.consultarPorIds(entrada.tipos());

        logger.debug(TipoSolicitudKey.LOG_CONSULTA_COMPLETADA, resultado.size());

        return resultado;
    }
}

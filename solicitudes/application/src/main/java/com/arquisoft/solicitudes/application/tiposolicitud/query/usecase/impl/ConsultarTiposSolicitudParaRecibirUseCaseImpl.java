package com.arquisoft.solicitudes.application.tiposolicitud.query.usecase.impl;

import com.arquisoft.solicitudes.application.tiposolicitud.query.criteria.TipoSolicitudParaRecibirCriteria;
import com.arquisoft.solicitudes.application.tiposolicitud.query.readmodel.TipoSolicitudReadModel;
import com.arquisoft.solicitudes.application.tiposolicitud.query.secondaryport.TipoSolicitudQueryOutputPort;
import com.arquisoft.solicitudes.application.tiposolicitud.query.usecase.ConsultarTiposSolicitudParaRecibirUseCase;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.solicitudes.TipoSolicitudKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarTiposSolicitudParaRecibirUseCaseImpl implements ConsultarTiposSolicitudParaRecibirUseCase {

    private final TipoSolicitudQueryOutputPort queryOutputPort;
    private final AppLogger logger;

    @Override
    public List<TipoSolicitudReadModel> ejecutar(TipoSolicitudParaRecibirCriteria entrada) {
        logger.debug(TipoSolicitudKey.LOG_CONSULTANDO_PARA_RECIBIR, entrada.actor(), entrada.rolesIdentidad());

        var resultado = queryOutputPort.consultarRecibiblesPorRoles(entrada.rolesIdentidad());

        logger.debug(TipoSolicitudKey.LOG_CONSULTA_PARA_RECIBIR_COMPLETADA, resultado.size());

        return resultado;
    }
}

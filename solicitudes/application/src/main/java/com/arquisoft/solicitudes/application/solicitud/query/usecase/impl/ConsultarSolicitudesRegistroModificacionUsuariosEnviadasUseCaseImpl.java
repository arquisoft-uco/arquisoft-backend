package com.arquisoft.solicitudes.application.solicitud.query.usecase.impl;

import com.arquisoft.solicitudes.application.solicitud.query.criteria.SolicitudCriteria;
import com.arquisoft.solicitudes.application.solicitud.query.readmodel.SolicitudReadModel;
import com.arquisoft.solicitudes.application.solicitud.query.secondaryport.SolicitudQueryOutputPort;
import com.arquisoft.solicitudes.application.solicitud.query.usecase.ConsultarSolicitudesRegistroModificacionUsuariosEnviadasUseCase;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.solicitudes.SolicitudKey;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConsultarSolicitudesRegistroModificacionUsuariosEnviadasUseCaseImpl
        implements ConsultarSolicitudesRegistroModificacionUsuariosEnviadasUseCase {

    private final SolicitudQueryOutputPort solicitudQueryOutputPort;
    private final AppLogger logger;

    @Override
    public PaginatedResult<SolicitudReadModel> ejecutar(SolicitudCriteria entrada) {
        logger.debug(SolicitudKey.LOG_CONSULTANDO_REGISTRO_MODIFICACION_USUARIOS_ENVIADAS,
                entrada.getPagina(), entrada.getTamanio(),
                entrada.tieneFiltros(), entrada.tieneOrden());

        var resultado = solicitudQueryOutputPort.consultar(entrada);

        logger.debug(SolicitudKey.LOG_CONSULTA_REGISTRO_MODIFICACION_USUARIOS_ENVIADAS_COMPLETADA,
                resultado.getTotalElements(), entrada.getPagina(), entrada.getTamanio());
        return resultado;
    }
}

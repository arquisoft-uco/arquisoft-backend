package com.arquisoft.solicitudes.application.solicitud.query.usecase.impl;

import com.arquisoft.solicitudes.application.solicitud.query.criteria.SolicitudCriteria;
import com.arquisoft.solicitudes.application.solicitud.query.readmodel.SolicitudReadModel;
import com.arquisoft.solicitudes.application.solicitud.query.secondaryport.SolicitudQueryOutputPort;
import com.arquisoft.solicitudes.application.solicitud.query.usecase.ConsultarSolicitudesNovedadAsesorRecibidasUseCase;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.solicitudes.SolicitudKey;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConsultarSolicitudesNovedadAsesorRecibidasUseCaseImpl
        implements ConsultarSolicitudesNovedadAsesorRecibidasUseCase {

    private final SolicitudQueryOutputPort solicitudQueryOutputPort;
    private final AppLogger logger;

    @Override
    public PaginatedResult<SolicitudReadModel> ejecutar(SolicitudCriteria entrada) {
        logger.debug(SolicitudKey.LOG_CONSULTANDO_NOVEDAD_ASESOR_RECIBIDAS,
                entrada.getPagina(), entrada.getTamanio(),
                entrada.tieneFiltros(), entrada.tieneOrden());

        var resultado = solicitudQueryOutputPort.consultar(entrada);

        logger.debug(SolicitudKey.LOG_CONSULTA_NOVEDAD_ASESOR_RECIBIDAS_COMPLETADA,
                resultado.getTotalElements(), entrada.getPagina(), entrada.getTamanio());
        return resultado;
    }
}

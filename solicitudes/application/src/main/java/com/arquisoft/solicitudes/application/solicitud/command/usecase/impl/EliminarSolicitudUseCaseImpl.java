package com.arquisoft.solicitudes.application.solicitud.command.usecase.impl;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.solicitudes.SolicitudKey;
import com.arquisoft.solicitudes.application.solicitud.command.finder.DatosSolicitudFinder;
import com.arquisoft.solicitudes.application.solicitud.command.finder.SolicitudTieneRespuestasFinder;
import com.arquisoft.solicitudes.application.solicitud.command.secondaryport.SolicitudOutputPort;
import com.arquisoft.solicitudes.application.solicitud.command.usecase.EliminarSolicitudUseCase;
import com.arquisoft.solicitudes.application.solicitud.command.validator.EliminarSolicitudValidator;
import com.arquisoft.solicitudes.domain.solicitud.EliminacionSolicitudDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EliminarSolicitudUseCaseImpl implements EliminarSolicitudUseCase {

    private final SolicitudOutputPort solicitudOutputPort;
    private final DatosSolicitudFinder datosSolicitudFinder;
    private final SolicitudTieneRespuestasFinder solicitudTieneRespuestasFinder;
    private final EliminarSolicitudValidator validator;
    private final AppLogger logger;

    @Override
    public void ejecutar(EliminacionSolicitudDomain entrada) {
        var tipo = entrada.getTipoEsperado().getId();
        logger.info(SolicitudKey.LOG_ELIMINANDO,
                tipo, entrada.getSolicitud(), entrada.getRemitenteUsuario());

        var resumen = datosSolicitudFinder.obtener(entrada.getSolicitud());
        var tieneRespuestas = solicitudTieneRespuestasFinder.obtener(entrada.getSolicitud());

        logger.debug(SolicitudKey.LOG_VERIFICACION_ELIMINACION, !resumen.esVacio(), tieneRespuestas);

        validator.validar(entrada, resumen, tieneRespuestas);

        solicitudOutputPort.eliminar(entrada.getSolicitud());

        logger.info(SolicitudKey.LOG_ELIMINADA, tipo, entrada.getSolicitud());
    }
}

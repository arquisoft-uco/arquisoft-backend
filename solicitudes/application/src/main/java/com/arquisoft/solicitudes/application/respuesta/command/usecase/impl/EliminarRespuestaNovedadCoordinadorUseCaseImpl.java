package com.arquisoft.solicitudes.application.respuesta.command.usecase.impl;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.solicitudes.RespuestaKey;
import com.arquisoft.solicitudes.application.respuesta.command.finder.DatosRespuestaFinder;
import com.arquisoft.solicitudes.application.respuesta.command.secondaryport.RespuestaOutputPort;
import com.arquisoft.solicitudes.application.respuesta.command.usecase.EliminarRespuestaNovedadCoordinadorUseCase;
import com.arquisoft.solicitudes.application.respuesta.command.validator.EliminarRespuestaNovedadCoordinadorValidator;
import com.arquisoft.solicitudes.application.solicitud.command.finder.DatosSolicitudFinder;
import com.arquisoft.solicitudes.domain.respuesta.EliminacionRespuestaNovedadCoordinadorDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EliminarRespuestaNovedadCoordinadorUseCaseImpl
        implements EliminarRespuestaNovedadCoordinadorUseCase {

    private final DatosSolicitudFinder datosSolicitudFinder;
    private final DatosRespuestaFinder datosRespuestaFinder;
    private final RespuestaOutputPort respuestaOutputPort;
    private final EliminarRespuestaNovedadCoordinadorValidator validator;
    private final AppLogger logger;

    @Override
    public void ejecutar(EliminacionRespuestaNovedadCoordinadorDomain entrada) {
        logger.info(RespuestaKey.LOG_ELIMINANDO,
                entrada.getSolicitud(), entrada.getCoordinadorUsuario());

        var resumenSolicitud = datosSolicitudFinder.obtener(entrada.getSolicitud());
        var resumenRespuesta = datosRespuestaFinder.obtener(entrada.getSolicitud());

        logger.debug(RespuestaKey.LOG_VERIFICACION_ELIMINACION,
                !resumenSolicitud.esVacio(), !resumenRespuesta.esVacio());

        validator.validar(entrada, resumenSolicitud, resumenRespuesta);

        respuestaOutputPort.eliminarPorSolicitud(entrada.getSolicitud());

        logger.info(RespuestaKey.LOG_ELIMINADA, entrada.getSolicitud());
    }
}

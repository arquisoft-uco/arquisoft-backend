package com.arquisoft.solicitudes.application.respuesta.command.usecase.impl;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.solicitudes.RespuestaKey;
import com.arquisoft.solicitudes.application.respuesta.command.finder.DatosRespuestaFinder;
import com.arquisoft.solicitudes.application.respuesta.command.secondaryport.RespuestaOutputPort;
import com.arquisoft.solicitudes.application.respuesta.command.usecase.EliminarRespuestaUseCase;
import com.arquisoft.solicitudes.application.respuesta.command.validator.EliminarRespuestaValidator;
import com.arquisoft.solicitudes.application.solicitud.command.finder.DatosSolicitudFinder;
import com.arquisoft.solicitudes.domain.respuesta.EliminacionRespuestaDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EliminarRespuestaUseCaseImpl implements EliminarRespuestaUseCase {

    private final DatosSolicitudFinder datosSolicitudFinder;
    private final DatosRespuestaFinder datosRespuestaFinder;
    private final RespuestaOutputPort respuestaOutputPort;
    private final EliminarRespuestaValidator validator;
    private final AppLogger logger;

    @Override
    public void ejecutar(EliminacionRespuestaDomain entrada) {
        var tipo = entrada.getTipoEsperado().getId();
        logger.info(RespuestaKey.LOG_ELIMINANDO,
                tipo, entrada.getSolicitud(), entrada.getResponsableUsuario());

        var resumenSolicitud = datosSolicitudFinder.obtener(entrada.getSolicitud());
        var resumenRespuesta = datosRespuestaFinder.obtener(entrada.getSolicitud());

        logger.debug(RespuestaKey.LOG_VERIFICACION_ELIMINACION,
                !resumenSolicitud.esVacio(), !resumenRespuesta.esVacio());

        validator.validar(entrada, resumenSolicitud, resumenRespuesta);

        respuestaOutputPort.eliminarPorSolicitud(entrada.getSolicitud());

        logger.info(RespuestaKey.LOG_ELIMINADA, tipo, entrada.getSolicitud());
    }
}

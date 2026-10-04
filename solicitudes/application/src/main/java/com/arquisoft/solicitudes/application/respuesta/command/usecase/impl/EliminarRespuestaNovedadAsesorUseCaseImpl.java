package com.arquisoft.solicitudes.application.respuesta.command.usecase.impl;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.solicitudes.RespuestaKey;
import com.arquisoft.solicitudes.application.respuesta.command.finder.DatosRespuestaFinder;
import com.arquisoft.solicitudes.application.respuesta.command.secondaryport.RespuestaOutputPort;
import com.arquisoft.solicitudes.application.respuesta.command.usecase.EliminarRespuestaNovedadAsesorUseCase;
import com.arquisoft.solicitudes.application.respuesta.command.validator.EliminarRespuestaNovedadAsesorValidator;
import com.arquisoft.solicitudes.application.solicitud.command.finder.DatosSolicitudFinder;
import com.arquisoft.solicitudes.domain.respuesta.EliminacionRespuestaNovedadAsesorDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EliminarRespuestaNovedadAsesorUseCaseImpl
        implements EliminarRespuestaNovedadAsesorUseCase {

    private final DatosSolicitudFinder datosSolicitudFinder;
    private final DatosRespuestaFinder datosRespuestaFinder;
    private final RespuestaOutputPort respuestaOutputPort;
    private final EliminarRespuestaNovedadAsesorValidator validator;
    private final AppLogger logger;

    @Override
    public void ejecutar(EliminacionRespuestaNovedadAsesorDomain entrada) {
        logger.info(RespuestaKey.LOG_ELIMINANDO_NOVEDAD_ASESOR,
                entrada.getSolicitud(), entrada.getAsesorUsuario());

        var resumenSolicitud = datosSolicitudFinder.obtener(entrada.getSolicitud());
        var resumenRespuesta = datosRespuestaFinder.obtener(entrada.getSolicitud());

        logger.debug(RespuestaKey.LOG_VERIFICACION_ELIMINACION,
                !resumenSolicitud.esVacio(), !resumenRespuesta.esVacio());

        validator.validar(entrada, resumenSolicitud, resumenRespuesta);

        respuestaOutputPort.eliminarPorSolicitud(entrada.getSolicitud());

        logger.info(RespuestaKey.LOG_ELIMINADA_NOVEDAD_ASESOR, entrada.getSolicitud());
    }
}

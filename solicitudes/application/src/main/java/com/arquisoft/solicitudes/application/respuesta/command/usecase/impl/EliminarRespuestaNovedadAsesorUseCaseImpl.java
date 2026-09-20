package com.arquisoft.solicitudes.application.respuesta.command.usecase.impl;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.solicitudes.RespuestaKey;
import com.arquisoft.shared.publisher.EventPublisher;
import com.arquisoft.solicitudes.application.respuesta.command.finder.DatosRespuestaFinder;
import com.arquisoft.solicitudes.application.respuesta.command.secondaryport.RespuestaOutputPort;
import com.arquisoft.solicitudes.application.respuesta.command.usecase.EliminarRespuestaNovedadAsesorUseCase;
import com.arquisoft.solicitudes.application.respuesta.command.validator.EliminarRespuestaNovedadAsesorValidator;
import com.arquisoft.solicitudes.application.solicitud.command.finder.DatosSolicitudFinder;
import com.arquisoft.solicitudes.domain.respuesta.EliminacionRespuestaNovedadAsesorDomain;
import com.arquisoft.solicitudes.domain.respuesta.event.SolicitudNovedadAsesorRespuestaEliminadaEvent;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;
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
    private final EventPublisher eventPublisher;
    private final AppLogger logger;

    @Override
    public void ejecutar(EliminacionRespuestaNovedadAsesorDomain entrada) {
        logger.info(RespuestaKey.LOG_ELIMINANDO_NOVEDAD_ASESOR,
                entrada.getSolicitud(), entrada.getAsesorUsuario());

        var resumenSolicitud = datosSolicitudFinder.obtener(entrada.getSolicitud());
        var existeSolicitud = !resumenSolicitud.esVacio();

        var resumenRespuesta = datosRespuestaFinder.obtener(entrada.getSolicitud());
        var existeRespuesta = !resumenRespuesta.esVacio();

        logger.debug(RespuestaKey.LOG_VERIFICACION_ELIMINACION, existeSolicitud, existeRespuesta);

        validator.validar(entrada.getSolicitud(), existeSolicitud, resumenSolicitud.tipoSolicitud(),
                resumenSolicitud.destinatarioUsuario(), entrada.getAsesorUsuario(),
                existeRespuesta, resumenRespuesta.estado());

        respuestaOutputPort.eliminarPorSolicitud(entrada.getSolicitud());

        eventPublisher.publish(new SolicitudNovedadAsesorRespuestaEliminadaEvent(
                entrada.getSolicitud(), entrada.getAsesorUsuario(),
                TipoSolicitud.NOVEDAD_PARA_EL_ASESOR.getId()));

        logger.info(RespuestaKey.LOG_ELIMINADA_NOVEDAD_ASESOR, entrada.getSolicitud());
    }
}

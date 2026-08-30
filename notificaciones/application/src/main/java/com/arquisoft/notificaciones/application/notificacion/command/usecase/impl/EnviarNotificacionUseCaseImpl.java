package com.arquisoft.notificaciones.application.notificacion.command.usecase.impl;

import com.arquisoft.notificaciones.application.notificacion.command.finder.NotificacionProcesadaFinder;
import com.arquisoft.notificaciones.application.notificacion.command.result.EnvioNotificacionResult;
import com.arquisoft.notificaciones.application.notificacion.command.result.mapper.EnvioNotificacionResultMapper;
import com.arquisoft.notificaciones.application.notificacion.command.secondaryport.EnvioNotificacionOutputPort;
import com.arquisoft.notificaciones.application.notificacion.command.secondaryport.NotificacionOutputPort;
import com.arquisoft.notificaciones.application.notificacion.command.secondaryport.mapper.NotificacionMapper;
import com.arquisoft.notificaciones.application.notificacion.command.secondaryport.model.DestinatarioNotificacion;
import com.arquisoft.notificaciones.application.notificacion.command.secondaryport.model.MensajeNotificacion;
import com.arquisoft.notificaciones.application.notificacion.command.secondaryport.model.ResultadoEntrega;
import com.arquisoft.notificaciones.application.notificacion.command.usecase.EnviarNotificacionUseCase;
import com.arquisoft.notificaciones.domain.notificacion.EnvioNotificacionDomain;
import com.arquisoft.notificaciones.domain.notificacion.NotificacionDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.key.notificaciones.NotificacionKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EnviarNotificacionUseCaseImpl implements EnviarNotificacionUseCase {

    private final NotificacionOutputPort notificacionOutputPort;
    private final NotificacionProcesadaFinder notificacionProcesadaFinder;
    private final EnvioNotificacionOutputPort envioNotificacionOutputPort;
    private final AppLogger logger;

    @Override
    public EnvioNotificacionResult ejecutar(EnvioNotificacionDomain entrada) {
        boolean yaProcesada = notificacionProcesadaFinder.obtener(entrada.getIdEvento());
        logger.debug(Mensajes.obtener(NotificacionKey.LOG_VERIFICACION_PREVIA),
                entrada.getIdEvento(), yaProcesada);

        if (yaProcesada) {
            return EnvioNotificacionResultMapper.toResultDuplicada(entrada.getIdEvento());
        }

        var notificacion = entrada.getNotificacion();
        var resultado = registrarEntrega(notificacion, entregar(entrada));

        notificacionOutputPort.guardar(NotificacionMapper.toEntity(notificacion));
        return resultado;
    }

    private ResultadoEntrega entregar(EnvioNotificacionDomain entrada) {
        return envioNotificacionOutputPort.enviar(MensajeNotificacion.textoPlano(
                new DestinatarioNotificacion(
                        entrada.getDestinatarioNombre(), entrada.getDestinatarioEmail()),
                entrada.getAsunto(),
                entrada.getCuerpo()));
    }

    private EnvioNotificacionResult registrarEntrega(
            NotificacionDomain notificacion, ResultadoEntrega entrega) {
        return switch (entrega) {
            case ResultadoEntrega.Entregada entregada -> {
                notificacion.marcarEnviada();
                yield EnvioNotificacionResultMapper.toResultEnviada(notificacion);
            }
            case ResultadoEntrega.Rechazada rechazada -> {
                notificacion.marcarFallida(rechazada.motivo());
                yield EnvioNotificacionResultMapper.toResultFallida(notificacion);
            }
        };
    }
}

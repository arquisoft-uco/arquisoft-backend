package com.arquisoft.notificaciones.application.notificacion.command.usecase.impl;

import com.arquisoft.notificaciones.application.notificacion.command.finder.NotificacionesReintentablesFinder;
import com.arquisoft.notificaciones.application.notificacion.command.finder.model.CriterioReintento;
import com.arquisoft.notificaciones.application.notificacion.command.result.ReintentoNotificacionesResult;
import com.arquisoft.notificaciones.application.notificacion.command.result.mapper.ReintentoNotificacionesResultMapper;
import com.arquisoft.notificaciones.application.notificacion.command.secondaryport.EnvioNotificacionOutputPort;
import com.arquisoft.notificaciones.application.notificacion.command.secondaryport.NotificacionOutputPort;
import com.arquisoft.notificaciones.application.notificacion.command.secondaryport.mapper.MensajeNotificacionMapper;
import com.arquisoft.notificaciones.application.notificacion.command.secondaryport.mapper.NotificacionMapper;
import com.arquisoft.notificaciones.application.notificacion.command.secondaryport.model.ResultadoEntrega;
import com.arquisoft.notificaciones.application.notificacion.command.usecase.ReintentarNotificacionesFallidasUseCase;
import com.arquisoft.notificaciones.domain.notificacion.NotificacionDomain;
import com.arquisoft.notificaciones.domain.notificacion.ReintentoNotificacionesDomain;
import com.arquisoft.notificaciones.domain.notificacion.model.EstadoNotificacion;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.key.notificaciones.NotificacionKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ReintentarNotificacionesFallidasUseCaseImpl
        implements ReintentarNotificacionesFallidasUseCase {

    private final NotificacionesReintentablesFinder notificacionesReintentablesFinder;
    private final NotificacionOutputPort notificacionOutputPort;
    private final EnvioNotificacionOutputPort envioNotificacionOutputPort;
    private final AppLogger logger;

    @Override
    public ReintentoNotificacionesResult ejecutar(ReintentoNotificacionesDomain reintento) {
        List<NotificacionDomain> pendientes = notificacionesReintentablesFinder.obtener(
                new CriterioReintento(reintento.getMaxIntentos(), reintento.getLimite()));

        logger.info(Mensajes.obtener(NotificacionKey.LOG_REINTENTO_INICIADO), pendientes.size());

        int reenviadas = 0;
        int fallidas = 0;
        for (NotificacionDomain notificacion : pendientes) {
            if (reenviar(notificacion)) {
                reenviadas++;
            } else {
                fallidas++;
            }
        }

        int agotadas = contarAgotadas(pendientes, reintento.getMaxIntentos());
        logger.info(Mensajes.obtener(NotificacionKey.LOG_REINTENTO_RESULTADO),
                reenviadas, fallidas, agotadas);

        return ReintentoNotificacionesResultMapper.toResult(reenviadas, fallidas, agotadas);
    }

    private boolean reenviar(NotificacionDomain notificacion) {
        notificacion.prepararReintento();

        var entrega = envioNotificacionOutputPort.enviar(
                MensajeNotificacionMapper.toMensaje(notificacion));

        boolean entregada = switch (entrega) {
            case ResultadoEntrega.Entregada ignorada -> {
                notificacion.marcarEnviada();
                yield true;
            }
            case ResultadoEntrega.Rechazada rechazada -> {
                notificacion.marcarFallida(rechazada.motivo());
                yield false;
            }
        };

        notificacionOutputPort.guardar(NotificacionMapper.toEntity(notificacion));
        return entregada;
    }

    private int contarAgotadas(List<NotificacionDomain> procesadas, int maxIntentos) {
        return (int) procesadas.stream()
                .filter(notificacion -> notificacion.getEstado() == EstadoNotificacion.FALLIDA
                        && notificacion.getIntentos() >= maxIntentos)
                .count();
    }
}

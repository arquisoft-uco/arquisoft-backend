package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.entregables.entregableproyectogrado;

import com.arquisoft.notificaciones.application.notificacion.command.primaryport.interactor.EnviarNotificacionInteractor;
import com.arquisoft.notificaciones.infrastructure.config.NotificacionesEntregablesQueueConfig;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.notificaciones.ConsumidorKey;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.arquisoft.shared.util.UtilColeccion;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
public class EntregableProyectoGradoCanceladoConsumer extends AbstractEntregableEstadoCambiadoConsumer {

    public EntregableProyectoGradoCanceladoConsumer(
            EnviarNotificacionInteractor enviarNotificacionInteractor,
            @Qualifier("rabbitObjectMapper") ObjectMapper objectMapper,
            AppLogger logger,
            GestorTraza gestorTraza) {
        super(enviarNotificacionInteractor, objectMapper, logger, gestorTraza);
    }

    @RabbitListener(queues = NotificacionesEntregablesQueueConfig.ENTREGABLE_CANCELADO_QUEUE)
    public void onEntregableCancelado(Message message, Channel channel) throws IOException {
        withCorrelation(message, channel, () -> {
            var payload = deserialize(message, EntregableProyectoGradoCanceladoPayload.class);

            logger.info(ConsumidorKey.LOG_ENTREGABLE_CANCELADO_RECIBIDO, payload.idEvento(), payload.entregableProyectoGrado(),
                    UtilColeccion.aplicarPorDefecto(payload.estudiantes()).size());

            notificar(payload.idEvento(), payload.nombreProyecto(), payload.version(),
                    payload.estadoEntregableNombre(), payload.estudiantes());
        });
    }
}

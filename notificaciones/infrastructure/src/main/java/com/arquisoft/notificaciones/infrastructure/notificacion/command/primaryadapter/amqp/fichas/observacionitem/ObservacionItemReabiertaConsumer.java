package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.fichas.observacionitem;

import com.arquisoft.notificaciones.application.notificacion.command.primaryport.interactor.EnviarNotificacionInteractor;
import com.arquisoft.notificaciones.application.notificacion.command.primaryport.model.EnviarNotificacionCommand;
import com.arquisoft.notificaciones.infrastructure.config.NotificacionesFichasQueueConfig;
import com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.AbstractNotificacionConsumer;
import com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.TipoNotificacionEvento;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.notificaciones.ConsumidorKey;
import com.arquisoft.shared.message.key.notificaciones.PlantillaKey;
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
public class ObservacionItemReabiertaConsumer extends AbstractNotificacionConsumer {

    private final EnviarNotificacionInteractor enviarNotificacionInteractor;

    public ObservacionItemReabiertaConsumer(
            EnviarNotificacionInteractor enviarNotificacionInteractor,
            @Qualifier("rabbitObjectMapper") ObjectMapper objectMapper,
            AppLogger logger,
            GestorTraza gestorTraza) {
        super(objectMapper, gestorTraza, logger);
        this.enviarNotificacionInteractor = enviarNotificacionInteractor;
    }

    @RabbitListener(queues = NotificacionesFichasQueueConfig.OBSERVACION_ITEM_REABIERTA_QUEUE)
    public void onObservacionItemReabierta(Message message, Channel channel) throws IOException {
        withCorrelation(message, channel, () -> {
            var payload = deserialize(message, ObservacionItemReabiertaPayload.class);
            var estudiantes = UtilColeccion.aplicarPorDefecto(payload.estudiantes());

            logger.info(
                    ConsumidorKey.LOG_OBSERVACION_ITEM_REABIERTA_RECIBIDO,
                    payload.observacionItemId(),
                    estudiantes.size());

            estudiantes.forEach(estudiante -> notificar(payload, estudiante));
        });
    }

    private void notificar(
            ObservacionItemReabiertaPayload payload,
            ObservacionItemReabiertaPayload.ContactoPayload estudiante) {
        registrar(enviarNotificacionInteractor.ejecutar(EnviarNotificacionCommand.crear(
                payload.idEvento(),
                TipoNotificacionEvento.OBSERVACION_ITEM_REABIERTA.getCodigo(),
                estudiante.nombre(),
                estudiante.email(),
                plantilla(PlantillaKey.ASUNTO_OBSERVACION_ITEM_REABIERTA, payload.tituloProyecto()),
                plantilla(PlantillaKey.CUERPO_OBSERVACION_ITEM_REABIERTA,
                        estudiante.nombre(), payload.tituloProyecto()),
                plantilla(PlantillaKey.PIE_GENERICO))));
    }
}

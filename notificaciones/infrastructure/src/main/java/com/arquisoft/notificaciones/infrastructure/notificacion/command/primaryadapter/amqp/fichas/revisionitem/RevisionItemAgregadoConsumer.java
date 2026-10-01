package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.fichas.revisionitem;

import com.arquisoft.notificaciones.application.notificacion.command.primaryport.interactor.EnviarNotificacionInteractor;
import com.arquisoft.notificaciones.application.notificacion.command.primaryport.model.EnviarNotificacionCommand;
import com.arquisoft.notificaciones.infrastructure.config.NotificacionesFichasQueueConfig;
import com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.AbstractNotificacionConsumer;
import com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.TipoNotificacionEvento;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.Mensajes;
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
import java.util.List;

@Component
public class RevisionItemAgregadoConsumer extends AbstractNotificacionConsumer {

    private final EnviarNotificacionInteractor enviarNotificacionInteractor;

    public RevisionItemAgregadoConsumer(
            EnviarNotificacionInteractor enviarNotificacionInteractor,
            @Qualifier("rabbitObjectMapper") ObjectMapper objectMapper,
            AppLogger logger,
            GestorTraza gestorTraza) {
        super(objectMapper, gestorTraza, logger);
        this.enviarNotificacionInteractor = enviarNotificacionInteractor;
    }

    @RabbitListener(queues = NotificacionesFichasQueueConfig.REVISION_ITEM_AGREGADO_QUEUE)
    public void onRevisionItemAgregado(Message message, Channel channel) throws IOException {
        withCorrelation(message, channel, () -> {
            RevisionItemAgregadoPayload payload =
                    deserialize(message, RevisionItemAgregadoPayload.class);

            List<RevisionItemAgregadoPayload.ContactoPayload> estudiantes =
                    UtilColeccion.aplicarPorDefecto(payload.estudiantes());

            logger.info(
                    Mensajes.obtener(ConsumidorKey.LOG_REVISION_ITEM_AGREGADO_RECIBIDO),
                    payload.itemId(),
                    estudiantes.size());

            estudiantes.forEach(estudiante -> notificar(payload, estudiante));
        });
    }

    private void notificar(
            RevisionItemAgregadoPayload payload,
            RevisionItemAgregadoPayload.ContactoPayload estudiante) {
        registrar(enviarNotificacionInteractor.ejecutar(EnviarNotificacionCommand.crear(
                payload.idEvento(),
                TipoNotificacionEvento.REVISION_ITEM_AGREGADO.getCodigo(),
                estudiante.nombre(),
                estudiante.email(),
                plantilla(PlantillaKey.ASUNTO_REVISION_ITEM_AGREGADA, payload.tituloProyecto()),
                plantilla(PlantillaKey.CUERPO_REVISION_ITEM_AGREGADA,
                        estudiante.nombre(), payload.tituloProyecto()),
                plantilla(PlantillaKey.PIE_GENERICO))));
    }
}

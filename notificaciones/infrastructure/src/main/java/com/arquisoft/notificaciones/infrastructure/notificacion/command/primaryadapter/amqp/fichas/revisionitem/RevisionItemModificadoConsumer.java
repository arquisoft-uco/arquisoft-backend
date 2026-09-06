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
public class RevisionItemModificadoConsumer extends AbstractNotificacionConsumer {

    private final EnviarNotificacionInteractor enviarNotificacionInteractor;

    public RevisionItemModificadoConsumer(
            EnviarNotificacionInteractor enviarNotificacionInteractor,
            @Qualifier("rabbitObjectMapper") ObjectMapper objectMapper,
            AppLogger logger,
            GestorTraza gestorTraza) {
        super(objectMapper, gestorTraza, logger);
        this.enviarNotificacionInteractor = enviarNotificacionInteractor;
    }

    @RabbitListener(queues = NotificacionesFichasQueueConfig.REVISION_ITEM_MODIFICADO_QUEUE)
    public void onRevisionItemModificado(Message message, Channel channel) throws IOException {
        withCorrelation(message, channel, () -> {
            RevisionItemModificadoPayload payload =
                    deserialize(message, RevisionItemModificadoPayload.class);

            List<RevisionItemModificadoPayload.ContactoPayload> estudiantes =
                    UtilColeccion.aplicarPorDefecto(payload.estudiantes());

            logger.info(
                    Mensajes.obtener(ConsumidorKey.LOG_REVISION_ITEM_MODIFICADO_RECIBIDO),
                    payload.itemId(),
                    estudiantes.size());

            estudiantes.forEach(estudiante -> notificar(payload, estudiante));
        });
    }

    private void notificar(
            RevisionItemModificadoPayload payload,
            RevisionItemModificadoPayload.ContactoPayload estudiante) {
        registrar(enviarNotificacionInteractor.ejecutar(EnviarNotificacionCommand.crear(
                payload.idEvento(),
                TipoNotificacionEvento.REVISION_ITEM_MODIFICADO.getCodigo(),
                estudiante.nombre(),
                estudiante.email(),
                plantilla(PlantillaKey.ASUNTO_REVISION_ITEM_MODIFICADA, payload.tituloProyecto()),
                plantilla(PlantillaKey.CUERPO_REVISION_ITEM_MODIFICADA,
                        estudiante.nombre(), payload.tituloProyecto(), payload.estadoRevisionNombre()),
                plantilla(PlantillaKey.PIE_GENERICO))));
    }
}

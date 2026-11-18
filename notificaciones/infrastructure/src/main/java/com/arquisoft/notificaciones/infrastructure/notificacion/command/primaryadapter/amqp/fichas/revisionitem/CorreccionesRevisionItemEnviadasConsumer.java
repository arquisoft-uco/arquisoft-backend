package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.fichas.revisionitem;

import com.arquisoft.notificaciones.application.notificacion.command.primaryport.interactor.EnviarNotificacionInteractor;
import com.arquisoft.notificaciones.application.notificacion.command.primaryport.model.EnviarNotificacionCommand;
import com.arquisoft.notificaciones.infrastructure.config.NotificacionesFichasQueueConfig;
import com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.AbstractNotificacionConsumer;
import com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.TipoNotificacionEvento;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.notificaciones.ConsumidorKey;
import com.arquisoft.shared.message.key.notificaciones.PlantillaKey;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
public class CorreccionesRevisionItemEnviadasConsumer extends AbstractNotificacionConsumer {

    private final EnviarNotificacionInteractor enviarNotificacionInteractor;

    public CorreccionesRevisionItemEnviadasConsumer(
            EnviarNotificacionInteractor enviarNotificacionInteractor,
            @Qualifier("rabbitObjectMapper") ObjectMapper objectMapper,
            AppLogger logger,
            GestorTraza gestorTraza) {
        super(objectMapper, gestorTraza, logger);
        this.enviarNotificacionInteractor = enviarNotificacionInteractor;
    }

    @RabbitListener(queues = NotificacionesFichasQueueConfig.REVISION_ITEM_CORRECCIONES_ENVIADAS_QUEUE)
    public void onCorreccionesRevisionItemEnviadas(Message message, Channel channel) throws IOException {
        withCorrelation(message, channel, () -> {
            CorreccionesRevisionItemEnviadasPayload payload =
                    deserialize(message, CorreccionesRevisionItemEnviadasPayload.class);

            logger.info(
                    ConsumidorKey.LOG_REVISION_ITEM_CORRECCIONES_ENVIADAS_RECIBIDO,
                    payload.revisionItemId(),
                    payload.fichaPerfilId());

            registrar(enviarNotificacionInteractor.ejecutar(EnviarNotificacionCommand.crear(
                    payload.idEvento(),
                    TipoNotificacionEvento.REVISION_ITEM_CORRECCIONES_ENVIADAS.getCodigo(),
                    payload.asesor().nombre(),
                    payload.asesor().email(),
                    plantilla(PlantillaKey.ASUNTO_REVISION_ITEM_CORRECCIONES_ENVIADAS, payload.tituloProyecto()),
                    plantilla(PlantillaKey.CUERPO_REVISION_ITEM_CORRECCIONES_ENVIADAS,
                            payload.asesor().nombre(), payload.tituloProyecto()),
                    plantilla(PlantillaKey.PIE_GENERICO))));
        });
    }
}

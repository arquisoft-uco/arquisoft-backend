package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.artefactos.versionartefacto;

import com.arquisoft.notificaciones.application.notificacion.command.primaryport.interactor.EnviarNotificacionInteractor;
import com.arquisoft.notificaciones.application.notificacion.command.primaryport.model.EnviarNotificacionCommand;
import com.arquisoft.notificaciones.infrastructure.config.NotificacionesArtefactosQueueConfig;
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
public class VersionArtefactoGeneradaConsumer extends AbstractNotificacionConsumer {

    private final EnviarNotificacionInteractor enviarNotificacionInteractor;

    public VersionArtefactoGeneradaConsumer(
            EnviarNotificacionInteractor enviarNotificacionInteractor,
            @Qualifier("rabbitObjectMapper") ObjectMapper objectMapper,
            AppLogger logger,
            GestorTraza gestorTraza) {
        super(objectMapper, gestorTraza, logger);
        this.enviarNotificacionInteractor = enviarNotificacionInteractor;
    }

    @RabbitListener(queues = NotificacionesArtefactosQueueConfig.VERSION_ARTEFACTO_GENERADA_QUEUE)
    public void onVersionArtefactoGenerada(Message message, Channel channel) throws IOException {
        withCorrelation(message, channel, () -> {
            var payload = deserialize(message, VersionArtefactoGeneradaPayload.class);

            logger.info(ConsumidorKey.LOG_VERSION_ARTEFACTO_GENERADA_RECIBIDO,
                    payload.artefacto(), payload.numeroVersion());

            registrar(enviarNotificacionInteractor.ejecutar(EnviarNotificacionCommand.crear(
                    payload.idEvento(),
                    TipoNotificacionEvento.VERSION_ARTEFACTO_GENERADA.getCodigo(),
                    payload.asesor().nombre(),
                    payload.asesor().email(),
                    plantilla(PlantillaKey.ASUNTO_VERSION_ARTEFACTO_GENERADA,
                            payload.numeroVersion(), payload.nombreDocumento()),
                    plantilla(PlantillaKey.CUERPO_VERSION_ARTEFACTO_GENERADA,
                            payload.asesor().nombre(), payload.numeroVersion(), payload.nombreDocumento()),
                    plantilla(PlantillaKey.PIE_GENERICO))));
        });
    }
}

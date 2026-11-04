package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.artefactos.revisionasesor;

import com.arquisoft.notificaciones.application.notificacion.command.primaryport.interactor.EnviarNotificacionInteractor;
import com.arquisoft.notificaciones.application.notificacion.command.primaryport.model.EnviarNotificacionCommand;
import com.arquisoft.notificaciones.infrastructure.config.NotificacionesArtefactosQueueConfig;
import com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.AbstractNotificacionConsumer;
import com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.TipoNotificacionEvento;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.notificaciones.ConsumidorKey;
import com.arquisoft.shared.message.key.notificaciones.PlantillaKey;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.arquisoft.shared.util.UtilTexto;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
public class RevisionAsesorEstadoCambiadoConsumer extends AbstractNotificacionConsumer {

    private static final String ESTADO_RESUELTA = "RESUELTA";

    private final EnviarNotificacionInteractor enviarNotificacionInteractor;

    public RevisionAsesorEstadoCambiadoConsumer(
            EnviarNotificacionInteractor enviarNotificacionInteractor,
            @Qualifier("rabbitObjectMapper") ObjectMapper objectMapper,
            AppLogger logger,
            GestorTraza gestorTraza) {
        super(objectMapper, gestorTraza, logger);
        this.enviarNotificacionInteractor = enviarNotificacionInteractor;
    }

    @RabbitListener(queues = NotificacionesArtefactosQueueConfig.REVISION_ASESOR_ESTADO_CAMBIADO_QUEUE)
    public void onRevisionAsesorEstadoCambiado(Message message, Channel channel) throws IOException {
        withCorrelation(message, channel, () -> {
            var payload = deserialize(message, RevisionAsesorEstadoCambiadoPayload.class);

            logger.info(ConsumidorKey.LOG_REVISION_ASESOR_ESTADO_CAMBIADO_RECIBIDO,
                    payload.revisionAsesorId(),
                    UtilTexto.enmascararCorreo(payload.asesorEmail()));

            if (!ESTADO_RESUELTA.equals(payload.estado())) {
                logger.info(ConsumidorKey.LOG_REVISION_ASESOR_ESTADO_CAMBIADO_IGNORADO,
                        payload.revisionAsesorId(), payload.estado());
                return;
            }

            registrar(enviarNotificacionInteractor.ejecutar(EnviarNotificacionCommand.crear(
                    payload.idEvento(),
                    TipoNotificacionEvento.REVISION_ASESOR_RESUELTA.getCodigo(),
                    payload.asesorNombre(),
                    payload.asesorEmail(),
                    plantilla(PlantillaKey.ASUNTO_REVISION_ASESOR_RESUELTA, payload.estudianteNombre()),
                    plantilla(PlantillaKey.CUERPO_REVISION_ASESOR_RESUELTA,
                            payload.asesorNombre(), payload.estudianteNombre()),
                    plantilla(PlantillaKey.PIE_GENERICO))));
        });
    }
}

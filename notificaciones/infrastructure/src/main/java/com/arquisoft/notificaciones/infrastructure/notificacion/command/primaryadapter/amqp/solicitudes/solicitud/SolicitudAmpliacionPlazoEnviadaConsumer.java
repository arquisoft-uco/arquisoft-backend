package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.solicitudes.solicitud;

import com.arquisoft.notificaciones.application.notificacion.command.primaryport.interactor.EnviarNotificacionInteractor;
import com.arquisoft.notificaciones.application.notificacion.command.primaryport.model.EnviarNotificacionCommand;
import com.arquisoft.notificaciones.infrastructure.config.NotificacionesSolicitudesQueueConfig;
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
public class SolicitudAmpliacionPlazoEnviadaConsumer extends AbstractNotificacionConsumer {

    private final EnviarNotificacionInteractor enviarNotificacionInteractor;

    public SolicitudAmpliacionPlazoEnviadaConsumer(
            EnviarNotificacionInteractor enviarNotificacionInteractor,
            @Qualifier("rabbitObjectMapper") ObjectMapper objectMapper,
            AppLogger logger,
            GestorTraza gestorTraza) {
        super(objectMapper, gestorTraza, logger);
        this.enviarNotificacionInteractor = enviarNotificacionInteractor;
    }

    @RabbitListener(queues = NotificacionesSolicitudesQueueConfig.AMPLIACION_PLAZO_QUEUE)
    public void onSolicitudAmpliacionPlazoEnviada(Message message, Channel channel) throws IOException {
        withCorrelation(message, channel, () -> {
            SolicitudAmpliacionPlazoEnviadaPayload payload =
                    deserialize(message, SolicitudAmpliacionPlazoEnviadaPayload.class);

            logger.info(
                    ConsumidorKey.LOG_SOLICITUD_AMPLIACION_PLAZO_RECIBIDO,
                    payload.solicitudId(),
                    UtilTexto.enmascararCorreo(payload.destinatarioEmail()));

            registrar(enviarNotificacionInteractor.ejecutar(EnviarNotificacionCommand.crear(
                    payload.idEvento(),
                    TipoNotificacionEvento.SOLICITUD_AMPLIACION_PLAZO.getCodigo(),
                    payload.destinatarioNombre(),
                    payload.destinatarioEmail(),
                    plantilla(
                            PlantillaKey.ASUNTO_SOLICITUD_AMPLIACION_PLAZO,
                            payload.remitenteNombre()),
                    plantilla(
                            PlantillaKey.CUERPO_SOLICITUD_AMPLIACION_PLAZO,
                            payload.destinatarioNombre(),
                            payload.remitenteNombre(),
                            payload.mensajeSolicitud()),
                    plantilla(PlantillaKey.PIE_GENERICO))));
        });
    }
}

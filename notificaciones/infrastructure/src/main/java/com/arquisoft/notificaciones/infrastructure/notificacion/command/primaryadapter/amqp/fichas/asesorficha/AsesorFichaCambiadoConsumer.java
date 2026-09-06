package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.fichas.asesorficha;

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
import com.arquisoft.shared.util.UtilTexto;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
public class AsesorFichaCambiadoConsumer extends AbstractNotificacionConsumer {

    private final EnviarNotificacionInteractor enviarNotificacionInteractor;

    public AsesorFichaCambiadoConsumer(
            EnviarNotificacionInteractor enviarNotificacionInteractor,
            @Qualifier("rabbitObjectMapper") ObjectMapper objectMapper,
            AppLogger logger,
            GestorTraza gestorTraza) {
        super(objectMapper, gestorTraza, logger);
        this.enviarNotificacionInteractor = enviarNotificacionInteractor;
    }

    @RabbitListener(queues = NotificacionesFichasQueueConfig.ASESOR_CAMBIADO_QUEUE)
    public void onAsesorFichaCambiado(Message message, Channel channel) throws IOException {
        withCorrelation(message, channel, () -> {
            AsesorFichaCambiadoPayload payload =
                    deserialize(message, AsesorFichaCambiadoPayload.class);

            logger.info(
                    Mensajes.obtener(ConsumidorKey.LOG_ASESOR_CAMBIADO_RECIBIDO),
                    payload.fichaPerfilId(),
                    UtilTexto.enmascararCorreo(payload.asesorEmail()));

            registrar(enviarNotificacionInteractor.ejecutar(EnviarNotificacionCommand.crear(
                    payload.idEvento(),
                    TipoNotificacionEvento.ASESOR_FICHA_CAMBIADO.getCodigo(),
                    payload.asesorNombre(),
                    payload.asesorEmail(),
                    plantilla(
                            PlantillaKey.ASUNTO_ASESOR_CAMBIADO,
                            payload.tituloProyecto()),
                    plantilla(
                            PlantillaKey.CUERPO_ASESOR_CAMBIADO,
                            payload.asesorNombre(),
                            payload.tituloProyecto()),
                    plantilla(PlantillaKey.PIE_GENERICO))));
        });
    }
}

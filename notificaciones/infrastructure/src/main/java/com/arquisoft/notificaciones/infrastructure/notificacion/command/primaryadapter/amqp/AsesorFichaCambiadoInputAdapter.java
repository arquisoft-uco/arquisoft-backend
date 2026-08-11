package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp;

import com.arquisoft.shared.message.key.notificaciones.ConsumidorKey;
import com.arquisoft.shared.message.key.notificaciones.PlantillaKey;
import com.arquisoft.notificaciones.application.notificacion.command.primaryport.interactor.EnviarNotificacionInteractor;
import com.arquisoft.notificaciones.application.notificacion.command.primaryport.model.EnviarNotificacionCommand;
import com.arquisoft.notificaciones.domain.notificacion.model.TipoNotificacion;
import com.arquisoft.notificaciones.infrastructure.config.NotificacionesFichasQueueConfig;
import com.arquisoft.shared.amqp.consumer.AbstractEventConsumer;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.CatalogoMensajes;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Traduce el evento de fichas en la notificacion que corresponde.
 *
 * <p>Aqui —y no en el caso de uso— se decide el texto del correo: agregar un evento nuevo es
 * agregar otro consumidor con su plantilla, sin tocar la aplicacion ni el dominio.
 */
@Component
public class AsesorFichaCambiadoInputAdapter extends AbstractEventConsumer {

    private final EnviarNotificacionInteractor enviarNotificacionInteractor;
    private final AppLogger logger;
    private final CatalogoMensajes catalogo;

    public AsesorFichaCambiadoInputAdapter(
            EnviarNotificacionInteractor enviarNotificacionInteractor,
            @Qualifier("rabbitObjectMapper") ObjectMapper objectMapper,
            AppLogger logger,
            CatalogoMensajes catalogo) {
        super(objectMapper);
        this.enviarNotificacionInteractor = enviarNotificacionInteractor;
        this.logger = logger;
        this.catalogo = catalogo;
    }

    @RabbitListener(queues = NotificacionesFichasQueueConfig.ASESOR_CAMBIADO_QUEUE)
    public void onAsesorFichaCambiado(Message message, Channel channel) throws IOException {
        withCorrelation(message, channel, () -> {
            AsesorFichaCambiadoPayload payload =
                    deserialize(message, AsesorFichaCambiadoPayload.class);

            logger.info(
                    catalogo.obtener(ConsumidorKey.LOG_ASESOR_CAMBIADO_RECIBIDO),
                    payload.fichaPerfilId(),
                    payload.asesorEmail());

            enviarNotificacionInteractor.ejecutar(new EnviarNotificacionCommand(
                    payload.idEvento(),
                    TipoNotificacion.ASESOR_FICHA_CAMBIADO,
                    payload.asesorNombre(),
                    payload.asesorEmail(),
                    catalogo.formatear(
                            PlantillaKey.ASUNTO_ASESOR_CAMBIADO,
                            payload.tituloProyecto()),
                    catalogo.formatear(
                            PlantillaKey.CUERPO_ASESOR_CAMBIADO,
                            payload.asesorNombre(),
                            payload.tituloProyecto())));
        });
    }
}

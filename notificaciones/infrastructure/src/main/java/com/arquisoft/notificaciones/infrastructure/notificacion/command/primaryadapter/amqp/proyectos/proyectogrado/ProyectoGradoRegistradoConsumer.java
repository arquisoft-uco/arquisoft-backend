package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.proyectos.proyectogrado;

import com.arquisoft.notificaciones.application.notificacion.command.primaryport.interactor.EnviarNotificacionInteractor;
import com.arquisoft.notificaciones.application.notificacion.command.primaryport.model.EnviarNotificacionCommand;
import com.arquisoft.notificaciones.infrastructure.config.NotificacionesProyectosQueueConfig;
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
public class ProyectoGradoRegistradoConsumer extends AbstractNotificacionConsumer {

    private final EnviarNotificacionInteractor enviarNotificacionInteractor;

    public ProyectoGradoRegistradoConsumer(
            EnviarNotificacionInteractor enviarNotificacionInteractor,
            @Qualifier("rabbitObjectMapper") ObjectMapper objectMapper,
            AppLogger logger,
            GestorTraza gestorTraza) {
        super(objectMapper, gestorTraza, logger);
        this.enviarNotificacionInteractor = enviarNotificacionInteractor;
    }

    @RabbitListener(queues = NotificacionesProyectosQueueConfig.PROYECTO_GRADO_REGISTRADO_QUEUE)
    public void onProyectoGradoRegistrado(Message message, Channel channel) throws IOException {
        withCorrelation(message, channel, () -> {
            var payload = deserialize(message, ProyectoGradoRegistradoPayload.class);
            var coordinador = payload.coordinador();

            logger.info(ConsumidorKey.LOG_PROYECTO_GRADO_REGISTRADO_RECIBIDO,
                    payload.proyectoGradoId(), payload.fichaPerfilId(),
                    UtilTexto.enmascararCorreo(coordinador.email()));

            registrar(enviarNotificacionInteractor.ejecutar(EnviarNotificacionCommand.crear(
                    payload.idEvento(),
                    TipoNotificacionEvento.PROYECTO_GRADO_REGISTRADO_COORDINADOR.getCodigo(),
                    coordinador.nombre(),
                    coordinador.email(),
                    plantilla(PlantillaKey.ASUNTO_PROYECTO_GRADO_REGISTRADO_COORDINADOR, payload.tituloProyecto()),
                    plantilla(PlantillaKey.CUERPO_PROYECTO_GRADO_REGISTRADO_COORDINADOR,
                            coordinador.nombre(), payload.tituloProyecto()),
                    plantilla(PlantillaKey.PIE_GENERICO))));
        });
    }
}

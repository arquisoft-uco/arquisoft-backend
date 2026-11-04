package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.proyectos.asesorproyectogrado;

import com.arquisoft.notificaciones.application.notificacion.command.primaryport.interactor.EnviarNotificacionInteractor;
import com.arquisoft.notificaciones.application.notificacion.command.primaryport.model.EnviarNotificacionCommand;
import com.arquisoft.notificaciones.infrastructure.config.NotificacionesProyectosQueueConfig;
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
public class AsesorProyectoGradoAsignadoConsumer extends AbstractNotificacionConsumer {

    private final EnviarNotificacionInteractor enviarNotificacionInteractor;

    public AsesorProyectoGradoAsignadoConsumer(
            EnviarNotificacionInteractor enviarNotificacionInteractor,
            @Qualifier("rabbitObjectMapper") ObjectMapper objectMapper,
            AppLogger logger,
            GestorTraza gestorTraza) {
        super(objectMapper, gestorTraza, logger);
        this.enviarNotificacionInteractor = enviarNotificacionInteractor;
    }

    @RabbitListener(queues = NotificacionesProyectosQueueConfig.ASESOR_PROYECTO_GRADO_ASIGNADO_QUEUE)
    public void onAsesorProyectoGradoAsignado(Message message, Channel channel) throws IOException {
        withCorrelation(message, channel, () -> {
            var payload = deserialize(message, AsesorProyectoGradoAsignadoPayload.class);

            logger.info(ConsumidorKey.LOG_ASESOR_PROYECTO_GRADO_ASIGNADO_RECIBIDO, payload.proyectoGradoId());

            registrar(enviarNotificacionInteractor.ejecutar(EnviarNotificacionCommand.crear(
                    payload.idEvento(),
                    TipoNotificacionEvento.ASESOR_PROYECTO_GRADO_ASIGNADO.getCodigo(),
                    payload.asesor().nombre(),
                    payload.asesor().email(),
                    plantilla(PlantillaKey.ASUNTO_ASESOR_PROYECTO_GRADO_ASIGNADO, payload.tituloProyecto()),
                    plantilla(PlantillaKey.CUERPO_ASESOR_PROYECTO_GRADO_ASIGNADO,
                            payload.asesor().nombre(), payload.tituloProyecto()),
                    plantilla(PlantillaKey.PIE_GENERICO))));
        });
    }
}

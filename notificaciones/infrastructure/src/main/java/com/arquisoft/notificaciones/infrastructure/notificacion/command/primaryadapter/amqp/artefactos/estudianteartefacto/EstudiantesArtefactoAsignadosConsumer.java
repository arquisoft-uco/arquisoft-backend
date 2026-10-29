package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.artefactos.estudianteartefacto;

import com.arquisoft.notificaciones.application.notificacion.command.primaryport.interactor.EnviarNotificacionInteractor;
import com.arquisoft.notificaciones.application.notificacion.command.primaryport.model.EnviarNotificacionCommand;
import com.arquisoft.notificaciones.infrastructure.config.NotificacionesArtefactosQueueConfig;
import com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.AbstractNotificacionConsumer;
import com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.TipoNotificacionEvento;
import com.arquisoft.shared.logger.AppLogger;
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

@Component
public class EstudiantesArtefactoAsignadosConsumer extends AbstractNotificacionConsumer {

    private final EnviarNotificacionInteractor enviarNotificacionInteractor;

    public EstudiantesArtefactoAsignadosConsumer(
            EnviarNotificacionInteractor enviarNotificacionInteractor,
            @Qualifier("rabbitObjectMapper") ObjectMapper objectMapper,
            AppLogger logger,
            GestorTraza gestorTraza) {
        super(objectMapper, gestorTraza, logger);
        this.enviarNotificacionInteractor = enviarNotificacionInteractor;
    }

    @RabbitListener(queues = NotificacionesArtefactosQueueConfig.ESTUDIANTES_ARTEFACTO_ASIGNADOS_QUEUE)
    public void onEstudiantesArtefactoAsignados(Message message, Channel channel) throws IOException {
        withCorrelation(message, channel, () -> {
            var payload = deserialize(message, EstudiantesArtefactoAsignadosPayload.class);

            var estudiantes =
                    UtilColeccion.aplicarPorDefecto(payload.estudiantes());

            logger.info(ConsumidorKey.LOG_ESTUDIANTES_ARTEFACTO_ASIGNADOS_RECIBIDO,
                    payload.idEvento(), estudiantes.size());

            estudiantes.forEach(estudiante -> notificar(payload, estudiante));
        });
    }

    private void notificar(
            EstudiantesArtefactoAsignadosPayload payload,
            EstudiantesArtefactoAsignadosPayload.ContactoPayload estudiante) {
        registrar(enviarNotificacionInteractor.ejecutar(EnviarNotificacionCommand.crear(
                payload.idEvento(),
                TipoNotificacionEvento.ESTUDIANTES_ARTEFACTO_ASIGNADOS.getCodigo(),
                estudiante.nombre(),
                estudiante.email(),
                plantilla(PlantillaKey.ASUNTO_ESTUDIANTES_ARTEFACTO_ASIGNADOS, payload.documento()),
                plantilla(PlantillaKey.CUERPO_ESTUDIANTES_ARTEFACTO_ASIGNADOS,
                        estudiante.nombre(), payload.asesor().nombre(), payload.documento()),
                plantilla(PlantillaKey.PIE_GENERICO))));
    }
}

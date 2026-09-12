package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.evaluaciones.evaluacioncualitativajurado;

import com.arquisoft.notificaciones.application.notificacion.command.primaryport.interactor.EnviarNotificacionInteractor;
import com.arquisoft.notificaciones.application.notificacion.command.primaryport.model.EnviarNotificacionCommand;
import com.arquisoft.notificaciones.infrastructure.config.NotificacionesEvaluacionesQueueConfig;
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
import java.util.List;

@Component
public class EvaluacionesCualitativasJuradoRegistradasConsumer extends AbstractNotificacionConsumer {

    private final EnviarNotificacionInteractor enviarNotificacionInteractor;

    public EvaluacionesCualitativasJuradoRegistradasConsumer(
            EnviarNotificacionInteractor enviarNotificacionInteractor,
            @Qualifier("rabbitObjectMapper") ObjectMapper objectMapper,
            AppLogger logger,
            GestorTraza gestorTraza) {
        super(objectMapper, gestorTraza, logger);
        this.enviarNotificacionInteractor = enviarNotificacionInteractor;
    }

    @RabbitListener(
            queues = NotificacionesEvaluacionesQueueConfig.EVALUACIONES_CUALITATIVAS_JURADO_REGISTRADAS_QUEUE)
    public void onEvaluacionesCualitativasJuradoRegistradas(Message message, Channel channel) throws IOException {
        withCorrelation(message, channel, () -> {
            EvaluacionesCualitativasJuradoRegistradasPayload payload =
                    deserialize(message, EvaluacionesCualitativasJuradoRegistradasPayload.class);

            List<EvaluacionesCualitativasJuradoRegistradasPayload.ContactoPayload> estudiantes =
                    UtilColeccion.aplicarPorDefecto(payload.estudiantes());

            logger.info(
                    ConsumidorKey.LOG_EVALUACIONES_CUALITATIVAS_JURADO_RECIBIDAS,
                    payload.evaluacionJuradoId(),
                    payload.cantidad(),
                    estudiantes.size());

            estudiantes.forEach(estudiante -> notificar(payload, estudiante));
        });
    }

    private void notificar(
            EvaluacionesCualitativasJuradoRegistradasPayload payload,
            EvaluacionesCualitativasJuradoRegistradasPayload.ContactoPayload estudiante) {
        registrar(enviarNotificacionInteractor.ejecutar(EnviarNotificacionCommand.crear(
                payload.idEvento(),
                TipoNotificacionEvento.EVALUACIONES_CUALITATIVAS_JURADO_REGISTRADAS.getCodigo(),
                estudiante.email(),
                estudiante.email(),
                plantilla(PlantillaKey.ASUNTO_EVALUACIONES_CUALITATIVAS_JURADO_REGISTRADAS, payload.proyecto()),
                plantilla(PlantillaKey.CUERPO_EVALUACIONES_CUALITATIVAS_JURADO_REGISTRADAS,
                        payload.cantidad(), payload.proyecto(), payload.versionEntregable()),
                plantilla(PlantillaKey.PIE_GENERICO))));
    }
}

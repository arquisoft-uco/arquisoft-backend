package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.evaluaciones.evaluacion;

import com.arquisoft.notificaciones.application.notificacion.command.primaryport.interactor.EnviarNotificacionInteractor;
import com.arquisoft.notificaciones.application.notificacion.command.primaryport.model.EnviarNotificacionCommand;
import com.arquisoft.notificaciones.infrastructure.config.NotificacionesEvaluacionesQueueConfig;
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
public class EvaluacionGeneradaConsumer extends AbstractNotificacionConsumer {

    private final EnviarNotificacionInteractor enviarNotificacionInteractor;

    public EvaluacionGeneradaConsumer(
            EnviarNotificacionInteractor enviarNotificacionInteractor,
            @Qualifier("rabbitObjectMapper") ObjectMapper objectMapper,
            AppLogger logger,
            GestorTraza gestorTraza) {
        super(objectMapper, gestorTraza, logger);
        this.enviarNotificacionInteractor = enviarNotificacionInteractor;
    }

    @RabbitListener(queues = NotificacionesEvaluacionesQueueConfig.EVALUACION_GENERADA_QUEUE)
    public void onEvaluacionGenerada(Message message, Channel channel) throws IOException {
        withCorrelation(message, channel, () -> {
            var payload = deserialize(message, EvaluacionGeneradaPayload.class);

            logger.info(ConsumidorKey.LOG_EVALUACION_GENERADA_RECIBIDO,
                    payload.idEvento(), payload.evaluacionId(), payload.entregableId());

            var coordinador = payload.coordinador();
            registrar(enviarNotificacionInteractor.ejecutar(EnviarNotificacionCommand.crear(
                    payload.idEvento(),
                    TipoNotificacionEvento.EVALUACION_GENERADA_COORDINADOR.getCodigo(),
                    coordinador.nombre(),
                    coordinador.email(),
                    plantilla(PlantillaKey.ASUNTO_EVALUACION_GENERADA, payload.nombreProyecto()),
                    plantilla(PlantillaKey.CUERPO_EVALUACION_GENERADA,
                            coordinador.nombre(), payload.versionEntregable(), payload.nombreProyecto()),
                    plantilla(PlantillaKey.PIE_GENERICO))));
        });
    }
}

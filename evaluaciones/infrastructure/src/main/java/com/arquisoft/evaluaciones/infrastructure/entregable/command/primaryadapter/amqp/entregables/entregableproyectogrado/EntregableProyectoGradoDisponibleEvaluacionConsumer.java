package com.arquisoft.evaluaciones.infrastructure.entregable.command.primaryadapter.amqp.entregables.entregableproyectogrado;

import com.arquisoft.evaluaciones.application.evaluacion.command.primaryport.interactor.GenerarEvaluacionInteractor;
import com.arquisoft.evaluaciones.application.evaluacion.command.primaryport.model.GenerarEvaluacionCommand;
import com.arquisoft.evaluaciones.application.evaluacion.command.result.GeneracionEvaluacionResult;
import com.arquisoft.evaluaciones.infrastructure.config.EvaluacionesEntregablesQueueConfig;
import com.arquisoft.shared.amqp.consumer.AbstractEventConsumer;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.EntregableKey;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionKey;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
public class EntregableProyectoGradoDisponibleEvaluacionConsumer extends AbstractEventConsumer {

    private final GenerarEvaluacionInteractor generarEvaluacionInteractor;
    private final AppLogger logger;

    public EntregableProyectoGradoDisponibleEvaluacionConsumer(
            GenerarEvaluacionInteractor generarEvaluacionInteractor,
            @Qualifier("rabbitObjectMapper") ObjectMapper objectMapper,
            AppLogger logger,
            GestorTraza gestorTraza) {
        super(objectMapper, gestorTraza, logger);
        this.generarEvaluacionInteractor = generarEvaluacionInteractor;
        this.logger = logger;
    }

    @RabbitListener(queues = EvaluacionesEntregablesQueueConfig.ENTREGABLE_DISPONIBLE_EVALUACION_QUEUE)
    public void onEntregableProyectoGradoDisponibleEvaluacion(Message message, Channel channel) throws IOException {
        withCorrelation(message, channel, () -> {
            var payload = deserialize(message, EntregableProyectoGradoDisponibleEvaluacionPayload.class);

            logger.info(EntregableKey.LOG_DISPONIBLE_RECIBIDO,
                    payload.idEvento(), payload.entregableProyectoGrado(), payload.proyectoGrado());

            var resultado = generarEvaluacionInteractor.ejecutar(GenerarEvaluacionCommand.crear(
                    payload.entregableProyectoGrado(), payload.proyectoGrado(), payload.nombreProyecto(),
                    payload.version()));

            switch (resultado) {
                case GeneracionEvaluacionResult.Generada generada ->
                        logger.info(EvaluacionKey.LOG_GENERADA, generada.evaluacion(), generada.entregable());
                case GeneracionEvaluacionResult.Duplicada duplicada ->
                        logger.info(EvaluacionKey.LOG_DUPLICADA, duplicada.entregable());
            }
        });
    }
}

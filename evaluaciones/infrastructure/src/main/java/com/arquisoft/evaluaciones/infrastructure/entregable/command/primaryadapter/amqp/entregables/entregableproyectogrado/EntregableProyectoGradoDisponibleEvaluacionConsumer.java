package com.arquisoft.evaluaciones.infrastructure.entregable.command.primaryadapter.amqp.entregables.entregableproyectogrado;

import com.arquisoft.evaluaciones.application.entregable.command.primaryport.interactor.AgregarEntregableInteractor;
import com.arquisoft.evaluaciones.application.entregable.command.primaryport.model.AgregarEntregableCommand;
import com.arquisoft.evaluaciones.application.entregable.command.result.AgregacionEntregableResult;
import com.arquisoft.evaluaciones.infrastructure.config.EvaluacionesEntregablesQueueConfig;
import com.arquisoft.shared.amqp.consumer.AbstractEventConsumer;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.EntregableKey;
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

    private final AgregarEntregableInteractor agregarEntregableInteractor;
    private final AppLogger logger;

    public EntregableProyectoGradoDisponibleEvaluacionConsumer(
            AgregarEntregableInteractor agregarEntregableInteractor,
            @Qualifier("rabbitObjectMapper") ObjectMapper objectMapper,
            AppLogger logger,
            GestorTraza gestorTraza) {
        super(objectMapper, gestorTraza, logger);
        this.agregarEntregableInteractor = agregarEntregableInteractor;
        this.logger = logger;
    }

    @RabbitListener(queues = EvaluacionesEntregablesQueueConfig.ENTREGABLE_DISPONIBLE_EVALUACION_QUEUE)
    public void onEntregableProyectoGradoDisponibleEvaluacion(Message message, Channel channel) throws IOException {
        withCorrelation(message, channel, () -> {
            var payload = deserialize(message, EntregableProyectoGradoDisponibleEvaluacionPayload.class);

            logger.info(EntregableKey.LOG_DISPONIBLE_RECIBIDO,
                    payload.idEvento(), payload.entregableProyectoGrado(), payload.proyectoGrado());

            var resultado = agregarEntregableInteractor.ejecutar(AgregarEntregableCommand.crear(
                    payload.entregableProyectoGrado(), payload.proyectoGrado(), payload.nombreProyecto(),
                    payload.version()));

            switch (resultado) {
                case AgregacionEntregableResult.Agregado agregado ->
                        logger.info(EntregableKey.LOG_AGREGADO, agregado.entregable());
                case AgregacionEntregableResult.Duplicado duplicado ->
                        logger.info(EntregableKey.LOG_DUPLICADO, duplicado.entregable());
            }
        });
    }
}

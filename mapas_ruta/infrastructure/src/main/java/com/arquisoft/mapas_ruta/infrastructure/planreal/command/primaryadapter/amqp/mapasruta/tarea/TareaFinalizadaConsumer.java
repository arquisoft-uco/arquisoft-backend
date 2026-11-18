package com.arquisoft.mapas_ruta.infrastructure.planreal.command.primaryadapter.amqp.mapasruta.tarea;

import com.arquisoft.mapas_ruta.application.planreal.command.primaryport.interactor.GenerarPlanRealInteractor;
import com.arquisoft.mapas_ruta.application.planreal.command.primaryport.model.GenerarPlanRealCommand;
import com.arquisoft.mapas_ruta.application.planreal.command.result.GeneracionPlanRealResult;
import com.arquisoft.mapas_ruta.infrastructure.config.MapasRutaTareaQueueConfig;
import com.arquisoft.shared.amqp.consumer.AbstractEventConsumer;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.mapas_ruta.PlanRealKey;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
public class TareaFinalizadaConsumer extends AbstractEventConsumer {

    private final GenerarPlanRealInteractor generarPlanRealInteractor;
    private final AppLogger logger;

    public TareaFinalizadaConsumer(
            GenerarPlanRealInteractor generarPlanRealInteractor,
            @Qualifier("rabbitObjectMapper") ObjectMapper objectMapper,
            AppLogger logger,
            GestorTraza gestorTraza) {
        super(objectMapper, gestorTraza, logger);
        this.generarPlanRealInteractor = generarPlanRealInteractor;
        this.logger = logger;
    }

    @RabbitListener(queues = MapasRutaTareaQueueConfig.TAREA_FINALIZADA_QUEUE)
    public void onTareaFinalizada(Message message, Channel channel) throws IOException {
        withCorrelation(message, channel, () -> {
            var payload = deserialize(message, TareaFinalizadaPayload.class);

            logger.info(PlanRealKey.LOG_TAREA_FINALIZADA_RECIBIDA, payload.idEvento(), payload.tarea());

            var resultado = generarPlanRealInteractor.ejecutar(
                    GenerarPlanRealCommand.crear(payload.tarea(), payload.ocurridoEn()));

            switch (resultado) {
                case GeneracionPlanRealResult.Generada generada ->
                        logger.info(PlanRealKey.LOG_GENERADO, generada.tarea(), generada.tiempoRealDias());
                case GeneracionPlanRealResult.Recalculada recalculada ->
                        logger.info(PlanRealKey.LOG_RECALCULADO, recalculada.tarea(), recalculada.tiempoRealDias());
                case GeneracionPlanRealResult.Descartada descartada ->
                        logger.info(PlanRealKey.LOG_DESCARTADO, descartada.tarea());
            }
        });
    }
}

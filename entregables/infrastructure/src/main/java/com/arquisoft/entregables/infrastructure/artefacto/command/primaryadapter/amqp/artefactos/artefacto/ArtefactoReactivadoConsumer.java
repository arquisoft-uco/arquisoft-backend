package com.arquisoft.entregables.infrastructure.artefacto.command.primaryadapter.amqp.artefactos.artefacto;

import com.arquisoft.entregables.application.artefacto.command.primaryport.interactor.ReactivarArtefactoInteractor;
import com.arquisoft.entregables.application.artefacto.command.primaryport.model.ReactivarArtefactoCommand;
import com.arquisoft.entregables.application.artefacto.command.result.ReactivacionArtefactoResult;
import com.arquisoft.entregables.infrastructure.config.EntregablesArtefactosQueueConfig;
import com.arquisoft.shared.amqp.RabbitListenerConfig;
import com.arquisoft.shared.amqp.consumer.AbstractEventConsumer;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.entregables.ArtefactoKey;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
public class ArtefactoReactivadoConsumer extends AbstractEventConsumer {

    private final ReactivarArtefactoInteractor reactivarArtefactoInteractor;
    private final AppLogger logger;

    public ArtefactoReactivadoConsumer(
            ReactivarArtefactoInteractor reactivarArtefactoInteractor,
            @Qualifier("rabbitObjectMapper") ObjectMapper objectMapper,
            AppLogger logger,
            GestorTraza gestorTraza) {
        super(objectMapper, gestorTraza, logger);
        this.reactivarArtefactoInteractor = reactivarArtefactoInteractor;
        this.logger = logger;
    }

    @RabbitListener(queues = EntregablesArtefactosQueueConfig.ARTEFACTO_REACTIVADO_QUEUE,
            concurrency = RabbitListenerConfig.UN_SOLO_CONSUMIDOR)
    public void onArtefactoReactivado(Message message, Channel channel) throws IOException {
        withCorrelation(message, channel, () -> {
            var payload = deserialize(message, ArtefactoReactivadoPayload.class);
            logger.info(ArtefactoKey.LOG_REACTIVACION_RECIBIDA,
                    payload.idEvento(), payload.artefacto(), payload.proyectoGrado());

            var resultado = reactivarArtefactoInteractor.ejecutar(
                    ReactivarArtefactoCommand.crear(
                            payload.artefacto(), payload.proyectoGrado(), payload.ocurridoEn()));

            switch (resultado) {
                case ReactivacionArtefactoResult.Reactivada reactivada ->
                        logger.info(ArtefactoKey.LOG_REACTIVADO, reactivada.artefacto());
                case ReactivacionArtefactoResult.Anticipada anticipada ->
                        logger.info(ArtefactoKey.LOG_LAPIDA_REGISTRADA, anticipada.artefacto());
                case ReactivacionArtefactoResult.Descartada descartada ->
                        logger.info(ArtefactoKey.LOG_DESCARTADA, descartada.artefacto());
            }
        });
    }
}

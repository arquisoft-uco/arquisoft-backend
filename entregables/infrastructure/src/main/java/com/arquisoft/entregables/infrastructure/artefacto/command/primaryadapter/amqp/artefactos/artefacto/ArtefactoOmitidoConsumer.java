package com.arquisoft.entregables.infrastructure.artefacto.command.primaryadapter.amqp.artefactos.artefacto;

import com.arquisoft.entregables.application.artefacto.command.primaryport.interactor.OmitirArtefactoInteractor;
import com.arquisoft.entregables.application.artefacto.command.primaryport.model.OmitirArtefactoCommand;
import com.arquisoft.entregables.application.artefacto.command.result.OmisionArtefactoResult;
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
public class ArtefactoOmitidoConsumer extends AbstractEventConsumer {

    private final OmitirArtefactoInteractor omitirArtefactoInteractor;
    private final AppLogger logger;

    public ArtefactoOmitidoConsumer(
            OmitirArtefactoInteractor omitirArtefactoInteractor,
            @Qualifier("rabbitObjectMapper") ObjectMapper objectMapper,
            AppLogger logger,
            GestorTraza gestorTraza) {
        super(objectMapper, gestorTraza, logger);
        this.omitirArtefactoInteractor = omitirArtefactoInteractor;
        this.logger = logger;
    }

    @RabbitListener(queues = EntregablesArtefactosQueueConfig.ARTEFACTO_OMITIDO_QUEUE,
            concurrency = RabbitListenerConfig.UN_SOLO_CONSUMIDOR)
    public void onArtefactoOmitido(Message message, Channel channel) throws IOException {
        withCorrelation(message, channel, () -> {
            var payload = deserialize(message, ArtefactoOmitidoPayload.class);
            logger.info(ArtefactoKey.LOG_OMISION_RECIBIDA,
                    payload.idEvento(), payload.artefacto(), payload.proyectoGrado());

            var resultado = omitirArtefactoInteractor.ejecutar(
                    OmitirArtefactoCommand.crear(
                            payload.artefacto(), payload.proyectoGrado(), payload.ocurridoEn()));

            switch (resultado) {
                case OmisionArtefactoResult.Omitida omitida ->
                        logger.info(ArtefactoKey.LOG_OMITIDO, omitida.artefacto());
                case OmisionArtefactoResult.Anticipada anticipada ->
                        logger.info(ArtefactoKey.LOG_LAPIDA_REGISTRADA, anticipada.artefacto());
                case OmisionArtefactoResult.Descartada descartada ->
                        logger.info(ArtefactoKey.LOG_DESCARTADA, descartada.artefacto());
            }
        });
    }
}

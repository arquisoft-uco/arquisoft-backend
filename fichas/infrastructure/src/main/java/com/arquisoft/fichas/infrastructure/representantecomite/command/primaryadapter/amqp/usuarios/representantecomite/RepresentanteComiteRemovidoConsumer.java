package com.arquisoft.fichas.infrastructure.representantecomite.command.primaryadapter.amqp.usuarios.representantecomite;

import com.arquisoft.fichas.application.representantecomite.command.primaryport.interactor.RemoverRepresentanteComiteInteractor;
import com.arquisoft.fichas.application.representantecomite.command.primaryport.model.RemoverRepresentanteComiteCommand;
import com.arquisoft.fichas.application.representantecomite.command.result.RemocionRepresentanteComiteResult;
import com.arquisoft.fichas.infrastructure.config.FichasUsuariosQueueConfig;
import com.arquisoft.shared.amqp.consumer.AbstractEventConsumer;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.RepresentanteComiteKey;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
public class RepresentanteComiteRemovidoConsumer extends AbstractEventConsumer {

    private final RemoverRepresentanteComiteInteractor removerRepresentanteComiteInteractor;
    private final AppLogger logger;

    public RepresentanteComiteRemovidoConsumer(
            RemoverRepresentanteComiteInteractor removerRepresentanteComiteInteractor,
            @Qualifier("rabbitObjectMapper") ObjectMapper objectMapper,
            AppLogger logger,
            GestorTraza gestorTraza) {
        super(objectMapper, gestorTraza);
        this.removerRepresentanteComiteInteractor = removerRepresentanteComiteInteractor;
        this.logger = logger;
    }

    @RabbitListener(queues = FichasUsuariosQueueConfig.REPRESENTANTE_COMITE_REMOVIDO_QUEUE)
    public void onRepresentanteComiteRemovido(Message message, Channel channel) throws IOException {
        withCorrelation(message, channel, () -> {
            var payload = deserialize(message, RepresentanteComiteRemovidoPayload.class);

            logger.info(RepresentanteComiteKey.LOG_REMOVIDO_RECIBIDO, payload.idEvento(), payload.usuario());

            var resultado = removerRepresentanteComiteInteractor.ejecutar(RemoverRepresentanteComiteCommand.crear(
                    payload.usuario(), payload.identificador(), payload.nombre(), payload.email(),
                    payload.ocurridoEn()));

            switch (resultado) {
                case RemocionRepresentanteComiteResult.Removida removida ->
                        logger.info(RepresentanteComiteKey.LOG_REMOVIDO, removida.representanteComite());
                case RemocionRepresentanteComiteResult.Lapida lapida ->
                        logger.info(RepresentanteComiteKey.LOG_LAPIDA, lapida.representanteComite());
                case RemocionRepresentanteComiteResult.Descartada descartada ->
                        logger.debug(RepresentanteComiteKey.LOG_REMOCION_DESCARTADA,
                                descartada.representanteComite(), descartada.ocurridoEnVigente());
            }
        });
    }
}
